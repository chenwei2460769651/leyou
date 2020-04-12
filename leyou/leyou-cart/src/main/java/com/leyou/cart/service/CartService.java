package com.leyou.cart.service;

import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.commom.utils.JsonUtils;
import com.leyou.common.pojo.UserInfo;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname CartService
 * @Description TODO
 * @Date 2020/4/9 21:57
 * @Created by chenwei
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    private static final String KEY_PREFIX = "user:cart";

    /**
     * 增加商品到购物车
     *
     * @param cart
     */
    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //查询购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        String key = cart.getSkuId().toString();
        Integer num = cart.getNum();
        //判断购物车的商品是否在购物车中
        if (hashOperations.hasKey(cart.getSkuId().toString())) {
            //在，更新数量
            String cartJson = hashOperations.get(key).toString();
            cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum() + num);
            hashOperations.put(key, JsonUtils.serialize(cart));
        } else {
            //不在，更新购物车
            Sku sku = this.goodsClient.querySkuBySkuId(cart.getSkuId());
            cart.setUserId(userInfo.getId());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            //如果多张图片就以，分割
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setPrice(sku.getPrice());
        }
        hashOperations.put(key, JsonUtils.serialize(cart));
    }
    /*
     * 描述：查询购物车
     * @Author 陈威
     * @Date 20:49 2020/4/10
     * @Param []
     *
     **/
    public List<Cart> queryCarts() {
        //通过拦截器得到用户信息  线程变量内存储的用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //判断用户是否有购物车记录
        if(!this.redisTemplate.hasKey(KEY_PREFIX+userInfo.getId())){
            return null;
        }
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        //获取购物车中Map所有Cart值集合
        List<Object> cartsJson = hashOperations.values();
        //如果购物车集合为空，返回null
        if(CollectionUtils.isEmpty(cartsJson)){
            return null;
        }
        //把List<Object>集合转化为List<Cart>集合
        //return cartsJson.stream().map(cartJson->JsonUtils.parse(cartsJson.toString(),Cart.class)).collect(Collectors.toList());
        List<Cart> carts = hashOperations.values().stream()
                .map(o -> JsonUtils.parse(o.toString(), Cart.class))
                .collect(Collectors.toList());
        return carts;
    }
    /*
     * 描述：修改商品数量
     * @Author 陈威
     * @Date 22:08 2020/4/10
     * @Param [cart]
     *
     **/
    /**
     * 更新购物车中商品的数量
     * @param cart
     */
    public void updateNum(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //判断用户是否有购物车记录
        if(!this.redisTemplate.hasKey(KEY_PREFIX+userInfo.getId())){
            return ;
        }
        //获取num
        Integer num = cart.getNum();
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        String  cartJson = hashOperations.get(cart.getSkuId().toString()).toString();
        //反序列化
        cart = JsonUtils.parse(cartJson, Cart.class);
        cart.setNum(num);
        hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    public void deleteCart(String skuId) {
        //获取登录用户
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX + userInfo.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        hashOps.delete(skuId);
    }
}
