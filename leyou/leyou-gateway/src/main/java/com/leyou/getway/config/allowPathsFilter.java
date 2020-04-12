package com.leyou.getway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Classname allowPaths
 * @Description TODO
 * @Date 2020/4/8 20:16
 * @Created by chenwei
 */
@ConfigurationProperties(prefix = "leyou.filter")
public class allowPathsFilter {
    private List<String> allowPaths;

    public List<String> getAllowPaths() {
        return allowPaths;
    }

    public void setAllowPaths(List<String> allowPaths) {
        this.allowPaths = allowPaths;
    }
}
