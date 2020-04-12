package com.leuou.search.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Classname BrandClient
 * @Description TODO
 * @Date 2020/3/26 10:48
 * @Created by chenwei
 */
@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
