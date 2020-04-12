package com.leyou.cart.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Classname GoodsClient
 * @Description TODO
 * @Date 2020/4/9 22:45
 * @Created by chenwei
 */
@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {
}
