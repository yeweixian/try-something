package com.dangerye.autodeliver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/autoDeliver")
public class AutoDeliverController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DiscoveryClient discoveryClient;

    /*
        @RequestMapping("/checkStatus/{userId}")
        public Integer checkStatusByUserId(@PathVariable Long userId) {
            return restTemplate.getForObject("http://localhost:8081/resume/status/" + userId, Integer.class);
        }
    */
    @RequestMapping("/checkStatus/{userId}")
    public Integer checkStatusByUserId(@PathVariable Long userId) {
        final List<ServiceInstance> instances = discoveryClient.getInstances("resume-application");
        final ServiceInstance serviceInstance = instances.get(0);
        final String host = serviceInstance.getHost();
        final int port = serviceInstance.getPort();
        final String url = "http://" + host + ":" + port + "/resume/status/" + userId;
        return restTemplate.getForObject(url, Integer.class);
    }
}
