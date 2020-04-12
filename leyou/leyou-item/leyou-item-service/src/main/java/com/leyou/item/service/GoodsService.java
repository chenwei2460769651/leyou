package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.commom.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname GoodsService
 * @Description TODO
 * @Date 2020/3/21 22:52
 * @Created by chenwei
 */
@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /*
     * 描述：商品列表 根据条件来分页查询spu
     * @Author 陈威
     * @Date 11:21 2020/3/22
     * @Param [key, saleable, page, rows]
     *
     **/
    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //添加查询条件  模糊查询  如果不为空就查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //添加sileable上下架过滤条件
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);

        }
        //添加分页
        PageHelper.startPage(page, rows);

        //执行查询，获取spu集合
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        //spu集合转化成spubo集合
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            //copy一个集合属性给另一个集合
            BeanUtils.copyProperties(spu, spuBo);

            //查询品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());

            //查询分类名称
            List<String> names = this.categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "-"));
            return spuBo;
        }).collect(Collectors.toList());

        //返回pageresult<spubo>
        return new PageResult<>(pageInfo.getTotal(), spuBos);

    }

    /*
     * 描述：新增商品
     * @Author 陈威
     * @Date 16:39 2020/3/22
     * @Param [spuBo]
     *
     **/
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        //先新增spu
        spuBo.setId(null);  //防止别人恶意注入数据
        spuBo.setSaleable(true); //设置默认上架
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());//系统当前时间
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);
        //在新增spu_detail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);
        //遍历新增sku
        saveSkuAndStock(spuBo);

        sendMsg("insert",spuBo.getId());

    }
    //新增消息对列方法
    private void sendMsg(String type,Long id) {
        try {
            this.amqpTemplate.convertAndSend("item."+type,id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            //新增sku
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);
            //新增sku_stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }
    /*
     * 描述：
     * @Author 陈威
     * @Date 18:36 2020/3/22
     * @Param
     *
     **/

    public SpuDetail querySpuDetailById(Long id) {
        return this.spuDetailMapper.selectByPrimaryKey(id);
    }

    /*
     * 描述：根据spuid查询sku集合
     * @Author 陈威
     * @Date 18:42 2020/3/22
     * @Param [spuId]
     *
     **/
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        //获取每个商品的库存信息
        skus.forEach(sku -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });
        return skus;
    }

    /*
     * 描述：更新商品
     * @Author 陈威
     * @Date 21:35 2020/3/22
     * @Param [spuBo]
     *
     **/
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        //查询要删除sku根据spuid
        Sku record = new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            //先删除sku_stock
            this.skuMapper.deleteByPrimaryKey(sku.getId());
        });

        //在删除sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        this.skuMapper.delete(sku);

        //新增sku,spu_stock  抽取的方法
        this.saveSkuAndStock(spuBo);
        //更新spu和spu_detail
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setSaleable(null); //上下架及是否有效不能更新
        spuBo.setValid(null);   //上下架及是否有效不能更新
        this.spuMapper.updateByPrimaryKeySelective(spuBo);
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        sendMsg("update",spuBo.getId());



    }
    /*
     * 描述：根据spuid查询spu集合
     * @Author 陈威
     * @Date 13:54 2020/4/2
     * @Param [id]
     *
     **/
    public Spu querySpuById(Long id) {
       return this.spuMapper.selectByPrimaryKey(id);
    }

    /*
     * 描述： 根据skuid查询sku
     * @Author 陈威
     * @Date 22:38 2020/4/9
     * @Param
     *
     **/
    public Sku querySkuBySkuId(Long skuId) {
        return  this.skuMapper.selectByPrimaryKey(skuId);
    }
}

