package com.dangerye.rpcapi.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Model {
    private Long id;
    private String name;
}
