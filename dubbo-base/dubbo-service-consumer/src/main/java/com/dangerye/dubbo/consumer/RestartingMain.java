package com.dangerye.dubbo.consumer;

import com.dangerye.base.utils.SingletonLoader;
import com.dangerye.router.utils.RestartServiceRiskControlUtil;

public class RestartingMain {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                final SingletonLoader<RestartServiceRiskControlUtil> singletonLoader = SingletonLoader.getSingletonLoader(RestartServiceRiskControlUtil.class);
                System.out.println(singletonLoader);
                final RestartServiceRiskControlUtil riskControlUtil = singletonLoader.getSingletonInstance("restartServiceRiskControlUtil");
                System.out.println(riskControlUtil);
            }).start();
        }
    }
}
