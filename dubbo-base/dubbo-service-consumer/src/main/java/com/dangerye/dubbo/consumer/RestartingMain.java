package com.dangerye.dubbo.consumer;

import com.dangerye.base.utils.SingletonLoader;
import com.dangerye.router.utils.RestartServiceRiskControlUtil;

public class RestartingMain {
    public static void main(String[] args) throws Exception {
        final SingletonLoader<RestartServiceRiskControlUtil> singletonLoader = SingletonLoader.getSingletonLoader(RestartServiceRiskControlUtil.class);
        final RestartServiceRiskControlUtil riskControlUtil = singletonLoader.getSingletonInstance("restartServiceRiskControlUtil");
        System.out.println(riskControlUtil);
    }
}
