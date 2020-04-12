package com.leuou.search.client;

import com.leyou.item.api.SpecifcationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Classname SpecifcationClient
 * @Description TODO
 * @Date 2020/3/26 10:49
 * @Created by chenwei
 */
@FeignClient("item-service")
public interface SpecificationClient extends SpecifcationApi {
}
