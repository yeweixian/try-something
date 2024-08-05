package com.dangerye.autodeliver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/autoDeliver")
public class AutoDeliverController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/checkStatus/{userId}")
    public Integer checkStatusByUserId(@PathVariable Long userId) {
        return restTemplate.getForObject("http://localhost:8081/resume/status/" + userId, Integer.class);
    }
}
