package com.leyou.goods.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Classname GoodsClient
 * @Description TODO
 * @Date 2020/3/26 10:07
 * @Created by chenwei
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
