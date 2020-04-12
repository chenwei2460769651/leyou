package com.leuou.search.client;

import com.leuou.search.pojo.Goods;
import com.leuou.search.repository.GoodsReponsitory;
import com.leuou.search.service.SearchService;
import com.leyou.commom.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname ElasticsearchTest
 * @Description TODO
 * @Date 2020/3/26 20:22
 * @Created by chenwei
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsReponsitory goodsReponsitory;

    @Autowired
    private SearchService searchService;

    @Autowired
    private  GoodsClient goodsClient;
    @Test
    public  void test(){
        //创建索引
        this.elasticsearchTemplate.createIndex(Goods.class);
        //创造映射
        this.elasticsearchTemplate.putMapping(Goods.class);
        //第一页
        Integer page=1;
        //每次导入一百条
        Integer rows=100;
        do {
            //分页查询spu，获取分页结果集
            PageResult<SpuBo> result = this.goodsClient.querySpuByPage(null, true, page, rows );
            //获取当前页数据
            List<SpuBo> items = result.getItems();
            //处理一个集合获取一个新的集合  List<Spubo>->List<Goods>
            List<Goods> goodSList = items.stream().map(spuBo -> {
                try {
                   return this.searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                 return null;
            }).collect(Collectors.toList());//返回新的数组
            //执行新增数据
            this.goodsReponsitory.saveAll(goodSList);
            //注入当前页数据 如果rows<100则不满足循坏条件
            rows = items.size();
            page++;
        } while (rows == 100);  //判断当前页数据是否达标
    }

}
