package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Classname UserClient
 * @Description TODO
 * @Date 2020/4/8 13:34
 * @Created by chenwei
 */
@FeignClient("user-service")
public interface UserClient extends UserApi {
}
