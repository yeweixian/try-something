package com.dangerye.rpcserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rpc2.server")
public class RpcConfigV2 {
    private Integer port;
}
