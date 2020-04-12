package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @Classname UserController
 * @Description TODO
 * @Date 2020/4/4 16:23
 * @Created by chenwei
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /*
     * 描述：校验数据是否可用
     * @Author 陈威
     * @Date 17:28 2020/4/4
     * @Param [data, type]
     *
     **/
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data") String data, @PathVariable("type") Integer type) {
        Boolean bool = this.userService.checkUser(data, type);
        if (bool == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }

    /**
     * 发送手机验证码
     *
     * @param phone
     * @return
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {
        this.userService.sendVerifyCode(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /*
     * 描述：接收用户注册信息，并对验证码进行校验
     * @Author 陈威
     * @Date 14:57 2020/4/7
     * @Param
     *
     **/
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code) {
        this.userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
     * 描述：查询用户
     * @Author 陈威
     * @Date 16:38 2020/4/7
     * @Param
     *
     **/
    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = this.userService.queryUser(username, password);
        if(user==null){
            return  ResponseEntity.badRequest().build();
        }
        return  ResponseEntity.ok(user);
    }
}
