package com.leuou.search.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Classname CategoryClient
 * @Description TODO
 * @Date 2020/3/26 10:49
 * @Created by chenwei
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
