package com.leyou.item.controller;

import com.leyou.commom.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Classname GoodsController
 * @Description TODO
 * @Date 2020/3/21 22:52
 * @Created by chenwei
 */
@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /*
     * 描述：商品列表 根据条件来分页查询spu
     * @Author 陈威
     * @Date 11:21 2020/3/22
     * @Param [key, saleable, page, rows]
     *
     **/
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,//上架下架
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    ) {
        PageResult<SpuBo> pageResult = this.goodsService.querySpuByPage(key, saleable, page, rows);
        if (CollectionUtils.isEmpty(pageResult.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pageResult);
    }

    /*
     * 描述：新增商品
     * @Author 陈威
     * @Date 18:31 2020/3/22
     * @Param [spuBo]
     *
     **/
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo) {
        this.goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /*
     * 描述：根据spuid查询spu_detail  商品修改的回显
     * @Author 陈威
     * @Date 18:35 2020/3/22
     * @Param [spuId]
     *
     **/
    @GetMapping("spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id") Long id) {
        SpuDetail spuDetail = this.goodsService.querySpuDetailById(id);
        if (spuDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    /*
     * 描述：根据spuid查询sku集合 商品修改的回显
     * @Author 陈威
     * @Date 18:42 2020/3/22
     * @Param [spuId]
     *
     **/
    @GetMapping("sku/list")

    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id") Long spuId) {
        List<Sku> skus = this.goodsService.querySkusBySpuId(spuId);
        if (CollectionUtils.isEmpty(skus)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);

    }

    /*
     * 描述：更新商品
     * @Author 陈威
     * @Date 21:10 2020/3/22
     * @Param [spuBo]
     *
     **/
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo) {
        this.goodsService.updateGoods(spuBo);
        //204  更新成功显示204
        return ResponseEntity.noContent().build();
    }

    /*
     * 描述：根据spuid查询spu集合
     * @Author 陈威
     * @Date 13:53 2020/4/2
     * @Param [id]
     *
     **/
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu = this.goodsService.querySpuById(id);
        if(spu == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(spu);
    }

    /*
     * 描述： 根据skuid查询sku
     * @Author 陈威
     * @Date 22:38 2020/4/9
     * @Param
     *
     **/
    @GetMapping("sku/{skuId}")
    public  ResponseEntity<Sku> querySkuBySkuId(@PathVariable("skuId")Long skuId){
        Sku sku= this.goodsService.querySkuBySkuId(skuId);
        if(sku == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(sku);
    }
}

