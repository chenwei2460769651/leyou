package com.leyou.cart.interceptor;

import com.leyou.cart.config.JwtProperties;
import com.leyou.commom.utils.CookieUtils;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Classname LoginInterceptor
 * @Description TODO
 * @Date 2020/4/9 20:46
 * @Created by chenwei
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor  extends HandlerInterceptorAdapter {

    @Autowired
    private  JwtProperties jwtProperties;
    private static final ThreadLocal<UserInfo>THREAD_LOCAL=new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        //解析token 获取用户信息
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
        if (userInfo==null){
            return false;
        }
        //放userinfo放入线程变量
        THREAD_LOCAL.set(userInfo);
        return true;
    }
    public static UserInfo getUserInfo(){
        return  THREAD_LOCAL.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ////清空线程里的局部变量，因为使用的是tomcat的线程池，线程不会结束，也就不久不会释放线程的局部变量
        THREAD_LOCAL.remove();
    }

}
