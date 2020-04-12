package com.leyou.item.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("category")
public interface CategoryApi {

    /*
     * 描述：根据商品分类id，查询商品分类名称
     * @Author 陈威
     * @Date 22:44 2020/3/25
     * @Param [ids]
     *
     **/
    @GetMapping
    public List<String> queryNameByIds(@RequestParam("ids") List<Long> ids);

}