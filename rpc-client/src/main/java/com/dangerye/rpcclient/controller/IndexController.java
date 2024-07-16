package com.dangerye.rpcclient.controller;

import com.dangerye.rpcapi.intf.TestService;
import com.dangerye.rpcapi.pojo.Model;
import com.dangerye.rpcclient.utils.RpcRemoteClientUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class IndexController {

    final ExecutorService executorService = Executors.newCachedThreadPool();
    @Autowired
    private RpcRemoteClientUtil rpcRemoteClientUtil;

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        final TestService testService = rpcRemoteClientUtil.createRemoteProxy(TestService.class);
        final List<Model> all = testService.findAll();
        System.out.println(all);
        System.out.println("------------");
        for (int i = 0; i < 128; i++) {
            final int n = i;
            final long l = RandomUtils.nextLong(0, 5);
            executorService.execute(() -> {
                final Model model = testService.findById(l == 0 ? null : l);
                System.out.println("--- thread i: " + n + " --- l: " + l + ", model: " + model);
            });
        }
        return "hello world!";
    }
}
