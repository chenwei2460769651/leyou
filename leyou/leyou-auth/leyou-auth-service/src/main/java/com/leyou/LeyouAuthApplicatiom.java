package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Classname LeyouAuthApplicatiom
 * @Description TODO
 * @Date 2020/4/8 0:18
 * @Created by chenwei
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LeyouAuthApplicatiom {
    public static void main(String[] args) {
        SpringApplication.run(LeyouAuthApplicatiom.class);
    }
}
