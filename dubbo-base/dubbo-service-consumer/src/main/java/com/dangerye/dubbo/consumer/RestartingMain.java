package com.dangerye.dubbo.consumer;

import com.dangerye.router.ReadyRestartInstances;

public class RestartingMain {
    public static void main(String[] args) throws Exception {
        final ReadyRestartInstances instance = ReadyRestartInstances.getInstance();
    }
}
