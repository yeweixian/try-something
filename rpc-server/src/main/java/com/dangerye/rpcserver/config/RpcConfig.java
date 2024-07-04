package com.dangerye.rpcserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rpc.server")
public class RpcConfig {
    private Integer port;
}
