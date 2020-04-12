package com.leyou.getway.filter;

import com.leyou.commom.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.getway.config.JwtProperties;
import com.leyou.getway.config.allowPathsFilter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Classname LoginFilter
 * @Description TODO
 * @Date 2020/4/8 19:34
 * @Created by chenwei
 */
@Component
@EnableConfigurationProperties({JwtProperties.class, allowPathsFilter.class})
public class LoginFilter extends ZuulFilter {
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private  allowPathsFilter allowPathsFilter;
    //过滤器类型  前置过滤器
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        //获取所有白名单路径
        List<String> allowPaths = this.allowPathsFilter.getAllowPaths();
        //初始化运行zuul网关的上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取请求参数
        HttpServletRequest request = context.getRequest();
        //获取请求路径
        String url = request.getRequestURI().toString();
        //把白名单和当前路径进行对比 是否包含 如果包含就不执行run方法 进行放行
        for (String allowpath:allowPaths){
            if (StringUtils.contains(url,allowpath)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //初始化运行zuul网关的上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取请求参数
        HttpServletRequest request = context.getRequest();
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        if(StringUtils.isBlank(token)){
            //setSendZuulResponse是否转发请求
            context.setSendZuulResponse(false);
            //设置响应状态码  401  授权未通过
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        try {
            //通过公钥解析token
            JwtUtils.getInfoFromToken(token,this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            //setSendZuulResponse是否转发请求
            context.setSendZuulResponse(false);
            //设置响应状态码  401  授权未通过
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
