package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.commom.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据查询条件分页并排序查询品牌信息
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */

    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        // 初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        // 根据name模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }

        // 添加分页条件
        PageHelper.startPage(page, rows);

        // 添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        List<Brand> brands = this.brandMapper.selectByExample(example);
        // 包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        // 包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }



    /**
     * 新增品牌
     *
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {

        // 先新增brand
        this.brandMapper.insertSelective(brand);

        // 在新增中间表
        cids.forEach(cid -> {
            this.brandMapper.insertCategoryAndBrand(cid, brand.getId());
        });
    }

    /*
     * 描述：商品列表新增商品基于分类的品牌查询
     * @Author 陈威
     * @Date 14:24 2020/3/22
     * @Param [cid]
     *
     **/
    public List<Brand> queryBrandsByCid(Long cid) {
      return   this.brandMapper.selectBrandsByCid(cid);

    }
    /**
     * 品牌的修改
     *
     * @param categories
     * @param brand
     */
    public void updateBrand(List<Long> categories, Brand brand) {
        //修改品牌
        brandMapper.updateByPrimaryKeySelective(brand);
        //维护中间表
        for (Long categoryId:categories) {
            brandMapper.updateCategoryBrand(categoryId,brand.getId());
        }
        }
    /*
     * 描述：删除品牌
     * @Author 陈威
     * @Date 20:33 2020/3/22
     * @Param [bid]
     *
     **/
    @Transactional
    public void deleteBrand(Long bid) {
        this.brandMapper.deleteByPrimaryKey(bid);
       brandMapper.deleteCategoryBrandByBid(bid);
    }
    /*
     * 描述：根据商品品牌id，查询商品的品牌名称
     * @Author 陈威
     * @Date 22:50 2020/3/25
     * @Param [id]
     *
     **/
    public Brand queryBrandById(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}

