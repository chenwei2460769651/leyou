package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("brand")
public interface BrandApi {

    /*
     * 描述：根据商品品牌id，查询商品的品牌名称
     * @Author 陈威
     * @Date 22:50 2020/3/25
     * @Param [id]
     *
     **/
    @GetMapping("{id}")
    public Brand queryBrandById(@PathVariable("id") Long id);

}