package com.dangerye.mywechatserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "netty")
public class WeChatConfig {
    private Integer port;
    private String path;
}
