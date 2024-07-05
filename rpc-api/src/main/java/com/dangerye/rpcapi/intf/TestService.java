package com.dangerye.rpcapi.intf;

import com.dangerye.rpcapi.pojo.Model;

import java.util.List;

public interface TestService {
    Model findById(Long id);

    List<Model> findAll();
}
