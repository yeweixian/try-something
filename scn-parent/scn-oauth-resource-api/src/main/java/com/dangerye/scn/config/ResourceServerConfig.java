package com.dangerye.scn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@Configuration
@EnableResourceServer   // 开启资源服务器功能
@EnableWebSecurity      // 开启web访问安全
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    /**
     * 用于定义资源服务器向远程认证服务器发起请求，进行token校验等事情
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // 定义 token 服务对象 token校验所需对应的服务对象
        final RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:7101/oauth/check_token");
        remoteTokenServices.setClientId("business_client");
        remoteTokenServices.setClientSecret("business_secret");
        resources.tokenServices(remoteTokenServices);
        // resources.resourceId() // 可以不用设置 也可以设置  -  resourceIds("autoDeliver", "resume")
        super.configure(resources);
    }

    /**
     * 一个服务中可能有很多资源（api接口）
     * 某一些接口需要先认证后访问，有些则不需要验证
     * 不需要验证的接口 需要 区分对待（即当前方法进行操作）
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // 设置 session 的创建策略  根据需要创建
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeRequests()
                .antMatchers("/autoDeliver/**").authenticated()
                .antMatchers("/resume/**").authenticated()
                .anyRequest().permitAll();
    }
}
