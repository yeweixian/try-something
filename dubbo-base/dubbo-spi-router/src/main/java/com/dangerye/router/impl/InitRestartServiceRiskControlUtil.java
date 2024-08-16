package com.dangerye.router.impl;

import com.dangerye.base.utils.SingletonInstance;
import com.dangerye.router.utils.RestartServiceRiskControlUtil;

public class InitRestartServiceRiskControlUtil implements SingletonInstance<RestartServiceRiskControlUtil> {
    @Override
    public RestartServiceRiskControlUtil getInstance() {
        return new RestartServiceRiskControlUtil();
    }
}
