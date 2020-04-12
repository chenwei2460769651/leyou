package com.leyou.user.service;

import com.leyou.commom.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Classname UserService
 * @Description TODO
 * @Date 2020/4/4 16:23
 * @Created by chenwei
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //短信验证码
    private static final String KEY_PREFIX = "user:verify";

    /*
     * 描述：校验数据是否可用
     * @Author 陈威
     * @Date 17:28 2020/4/4
     * @Param [data, type]
     *
     **/
    public Boolean checkUser(String data, Integer type) {
        User record = new User();
        if (type == 1) {
            record.setUsername(data);
        } else if (type == 2) {
            record.setPhone(data);
        } else {
            return null;
        }
        return this.userMapper.selectCount(record) == 0;
    }

    public void sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone)) {
            return;
        }
        //生成六位验证码
        String code = NumberUtils.generateCode(6);
        //发送消息的消息队列 rabbitmq
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        this.amqpTemplate.convertAndSend("leyou.sms.exchange", "verifycode.sms", msg);
        //把验证码保存到redis
        this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 1, TimeUnit.MINUTES);


    }

    /*
     * 描述：接收用户注册信息，并对验证码进行校验
     * @Author 陈威
     * @Date 14:57 2020/4/7
     * @Param
     *
     **/
    public void register(User user, String code) {
        //查询redis中的验证码
        String rediscode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        //1.校验验证码
        if (!StringUtils.equals(code, rediscode)) {
            return;
        }

        //2.生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //3.加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        //新增用户
        user.setId(null);
        user.setCreated(new Date());
        this.userMapper.insertSelective(user);

    }
    /*
     * 描述：查询用户
     * @Author 陈威
     * @Date 16:38 2020/4/7
     * @Param
     *
     **/
    public User queryUser(String username, String password) {
        User record=new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        //判断user是否为空
        if (user == null) {
            return null;
        }
        //获取盐，对用户输入的密码进行加盐加密
        password= CodecUtils.md5Hex(password,user.getSalt());
        if (StringUtils.equals(password,user.getPassword())){
            return  user;
        }
        return null;
    }
}
