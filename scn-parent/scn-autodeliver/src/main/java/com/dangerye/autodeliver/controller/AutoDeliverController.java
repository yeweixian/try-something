package com.dangerye.autodeliver.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
    /*
    @RequestMapping("/checkStatus/{userId}")
    public Integer checkStatusByUserId(@PathVariable Long userId) {
        final List<ServiceInstance> instances = discoveryClient.getInstances("resume-application");
        final ServiceInstance serviceInstance = instances.get(0);
        final String host = serviceInstance.getHost();
        final int port = serviceInstance.getPort();
        final String url = "http://" + host + ":" + port + "/resume/status/" + userId;
        System.out.println("url: " + url);
        return restTemplate.getForObject(url, Integer.class);
    }
    */
    @RequestMapping("/checkStatus/{userId}")
    public Integer checkStatusByUserId(@PathVariable Long userId) {
        final String url = "http://resume-application/resume/status/" + userId;
        return restTemplate.getForObject(url, Integer.class);
    }

    @RequestMapping("/checkStatusTimeOut/{userId}")
    @HystrixCommand(threadPoolKey = "checkStatusTimeOutByUserId", threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "2"),
            @HystrixProperty(name = "maxQueueSize", value = "20")
    }, commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })
    public Integer checkStatusTimeOutByUserId(@PathVariable Long userId) {
        final String url = "http://resume-application/resume/status/" + userId;
        return restTemplate.getForObject(url, Integer.class);
    }

    @RequestMapping("/checkStatusFallback/{userId}")
    @HystrixCommand(threadPoolKey = "checkStatusFallbackByUserId", threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "2"),
            @HystrixProperty(name = "maxQueueSize", value = "20")
    }, commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    }, fallbackMethod = "fallbackCheckStatusByUserId")
    public Integer checkStatusFallbackByUserId(@PathVariable Long userId) {
        final String url = "http://resume-application/resume/status/" + userId;
        return restTemplate.getForObject(url, Integer.class);
    }

    public Integer fallbackCheckStatusByUserId(@PathVariable Long userId) {
        return -1;
    }
}
