package com.leyou.item.api;

import com.leyou.commom.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {

    /**
     * 分页查询商品
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
    PageResult<SpuBo> querySpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", defaultValue = "true") Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows);

    /**
     * 根据spu商品id查询详情
     * @param id
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    SpuDetail querySpuDetailById(@PathVariable("id") Long id);

    /**
     * 根据spu的id查询sku
     * @param id
     * @return
     */
    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long id);
    /*
     * 描述：根据spuid查询spu集合
     * @Author 陈威
     * @Date 13:53 2020/4/2
     * @Param [id]
     *
     **/
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id")Long id);

    /*
     * 描述： 根据skuid查询sku
     * @Author 陈威
     * @Date 22:38 2020/4/9
     * @Param
     *
     **/
    @GetMapping("sku/{skuId}")
    public Sku querySkuBySkuId(@PathVariable("skuId")Long skuId);
}