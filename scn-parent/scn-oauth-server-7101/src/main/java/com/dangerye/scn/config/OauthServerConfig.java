package com.dangerye.scn.config;

import com.dangerye.scn.utils.Loader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration      // 当前为 oauth2 server 配置类
@EnableAuthorizationServer      // 开启认证服务器功能
public class OauthServerConfig extends AuthorizationServerConfigurerAdapter {
    private final Loader<TokenStore> tokenStoreLoader = new Loader<>();
    private final Loader<AuthorizationServerTokenServices> tokenServicesLoader = new Loader<>();
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 认证服务器 最终以api接口方式 对外提供服务 - 需校验合法性 生成令牌 及 校验令牌 等
     * 对外的接口服务 涉及接口访问权限 需要在此方法进行必要配置；
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        super.configure(security);
        // 打开endpoints 访问接口 的开关
        security
                // 允许 客户端 表单认证
                .allowFormAuthenticationForClients()
                // 开启端口访问权限(/oauth/token_key) 默认是拒绝访问
                .tokenKeyAccess("permitAll()")
                // 开启端口访问权限(/oauth/check_token) 默认是拒绝访问
                .checkTokenAccess("permitAll()");
    }

    /**
     * 客户端详情配置
     * 入参： client id 、secret
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        super.configure(clients);
        clients.inMemory()
                .withClient("business_client")
                .secret("business_secret")
                .resourceIds("autoDeliver", "resume")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("all");
    }

    /**
     * 配置 token 令牌相关的 方法
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        super.configure(endpoints);
        endpoints.tokenStore(tokenStore())
                .tokenServices(authorizationServerTokenServices())
                .authenticationManager(authenticationManager)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    private AuthorizationServerTokenServices authorizationServerTokenServices() {
        return tokenServicesLoader.getInstance(() -> {
            final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
            defaultTokenServices.setSupportRefreshToken(true);
            defaultTokenServices.setTokenStore(tokenStore());
            defaultTokenServices.setAccessTokenValiditySeconds(30);
            defaultTokenServices.setRefreshTokenValiditySeconds(3 * 24 * 60 * 60);
            return defaultTokenServices;
        });
    }

    private TokenStore tokenStore() {
        return tokenStoreLoader.getInstance(InMemoryTokenStore::new);
    }
}
