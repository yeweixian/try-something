package com.dangerye.rpcserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rpc.server")
public class RpcConfig {
    private Integer port;
}
