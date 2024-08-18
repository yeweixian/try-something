package com.dangerye.scn.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "scn.resources")
public class ResourceConfig {
    private String resourceId;
}
