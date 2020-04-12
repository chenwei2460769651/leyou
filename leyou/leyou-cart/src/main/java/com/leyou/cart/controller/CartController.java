package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Classname CartController
 * @Description TODO
 * @Date 2020/4/9 21:56
 * @Created by chenwei
 */
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /*
     * 描述：购物车商品添加   接受前台传来的购物车商品数据
     * @Author 陈威
     * @Date 22:05 2020/4/9
     * @Param [cart]
     *
     **/
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        this.cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
     * 描述：查询购物车
     * @Author 陈威
     * @Date 20:49 2020/4/10
     * @Param []
     *
     **/
    @GetMapping
    public ResponseEntity<List<Cart>> queryCarts() {
        List<Cart> carts = this.cartService.queryCarts();
        if (CollectionUtils.isEmpty(carts)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carts);
    }
    /*
     * 描述：修改商品数量
     * @Author 陈威
     * @Date 22:08 2020/4/10
     * @Param [cart]
     *
     **/
    @PutMapping
    public ResponseEntity<Void> updateNum(Cart cart){
        this.cartService.updateNum(cart);
        return ResponseEntity.noContent().build();
    }
    /**
     * 删除商品信息
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId")String skuId){
        this.cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }
}
