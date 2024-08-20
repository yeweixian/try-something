package com.dangerye.scn.config;

import com.dangerye.scn.utils.Loader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer   // 开启资源服务器功能
@EnableWebSecurity      // 开启web访问安全
@EnableConfigurationProperties(ResourceConfig.class)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    private final Loader<TokenStore> tokenStoreLoader = new Loader<>();
    private final Loader<JwtAccessTokenConverter> jwtAccessTokenConverterLoader = new Loader<>();
    @Autowired
    private ResourceConfig resourceConfig;

    /**
     * 用于定义资源服务器向远程认证服务器发起请求，进行token校验等事情
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // 定义 token 服务对象 token校验所需对应的服务对象
        // final RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        // remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:7101/oauth/check_token");
        // remoteTokenServices.setClientId("business_client");
        // remoteTokenServices.setClientSecret("business_secret");
        // resources.tokenServices(remoteTokenServices);
        resources.tokenStore(tokenStore()).stateless(true);
        if (StringUtils.isEmpty(resourceConfig.getResourceId())) {
            throw new RuntimeException("ConfigurationProperties: scn.resources.resourceId not find...");
        }
        resources.resourceId(resourceConfig.getResourceId());
        super.configure(resources);
    }

    private TokenStore tokenStore() {
        return tokenStoreLoader.getInstance(() -> new JwtTokenStore(jwtAccessTokenConverter()));
    }

    private JwtAccessTokenConverter jwtAccessTokenConverter() {
        return jwtAccessTokenConverterLoader.getInstance(() -> {
            final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
            jwtAccessTokenConverter.setSigningKey("signingKey");
            jwtAccessTokenConverter.setVerifier(new MacSigner("signingKey"));
            return jwtAccessTokenConverter;
        });
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
                .antMatchers("/rapi/**").authenticated()
                .anyRequest().permitAll();
    }
}
