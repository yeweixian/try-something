package com.dangerye.rpcapi.intf;

import com.dangerye.rpcapi.pojo.Model;

public interface TestService {
    Model findById(Long id);
}
