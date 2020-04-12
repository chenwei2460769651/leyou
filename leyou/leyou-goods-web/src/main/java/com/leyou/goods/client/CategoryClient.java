package com.leyou.goods.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Classname CategoryClient
 * @Description TODO
 * @Created by chenwei
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
