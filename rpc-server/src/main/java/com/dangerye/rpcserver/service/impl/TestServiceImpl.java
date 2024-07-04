package com.dangerye.rpcserver.service.impl;

import com.dangerye.rpcapi.anno.RpcService;
import com.dangerye.rpcapi.intf.TestService;
import com.dangerye.rpcapi.pojo.Model;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RpcService
public class TestServiceImpl implements TestService {

    private final Map<Long, Model> modelMap = new HashMap<>();

    public TestServiceImpl() {
        final Model model1 = Model.builder().id(1L).name("张三").build();
        final Model model2 = Model.builder().id(2L).name("李四").build();
        modelMap.put(model1.getId(), model1);
        modelMap.put(model2.getId(), model2);
    }

    @Override
    public Model findById(Long id) {
        return modelMap.get(id);
    }
}
