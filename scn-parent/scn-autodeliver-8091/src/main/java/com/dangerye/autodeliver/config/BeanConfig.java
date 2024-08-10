package com.dangerye.autodeliver.config;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ServletRegistrationBean<HystrixMetricsStreamServlet> getServletRegistrationBean() {
        final HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        final ServletRegistrationBean<HystrixMetricsStreamServlet> registrationBean = new ServletRegistrationBean<>(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/actuator/hystrix.stream");
        registrationBean.setName("hystrixMetricsStreamServlet");
        return registrationBean;
    }
}
