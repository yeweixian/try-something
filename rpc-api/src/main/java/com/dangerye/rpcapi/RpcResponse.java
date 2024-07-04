package com.dangerye.rpcapi;

import lombok.Data;

@Data
public class RpcResponse {
    private String requestId;
    private String errorMsg;
    private Object result;
}
