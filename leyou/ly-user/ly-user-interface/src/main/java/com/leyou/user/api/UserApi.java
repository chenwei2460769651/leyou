package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Classname UserApi
 * @Description TODO
 * @Date 2020/4/8 13:32
 * @Created by chenwei
 */
public interface UserApi {
    /*
     * 描述：查询用户
     * @Author 陈威
     * @Date 16:38 2020/4/7
     * @Param
     *
     **/
    @GetMapping("query")
    public User queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password);
}
