package com.leuou.search.repository;

import com.leuou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Classname GoodsRepository
 * @Description TODO
 * @Date 2020/3/26 20:25
 * @Created by chenwei
 */
public interface GoodsReponsitory extends ElasticsearchRepository<Goods, Long> {
}
