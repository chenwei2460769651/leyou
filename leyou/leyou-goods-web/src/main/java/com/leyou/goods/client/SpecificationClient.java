package com.leyou.goods.client;

import com.leyou.item.api.SpecifcationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Classname SpecifcationClient
 * @Description TODO
 * @Date 2020/3/26 10:49
 * @Created by chenwei
 */
@FeignClient(value = "item-service")
public interface SpecificationClient extends SpecifcationApi{
}
