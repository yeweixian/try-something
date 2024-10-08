package com.dangerye.scn.config;

import com.dangerye.scn.service.JdbcUserDetailsService;
import com.dangerye.scn.utils.Loader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 配置类 用于校验用户名 和 密码的校验 相关操作
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final Loader<PasswordEncoder> passwordEncoderLoader = new Loader<>();
    @Autowired
    private JdbcUserDetailsService jdbcUserDetailsService;

    /**
     * 注册一个认证管理器对象到 spring 容器里
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return passwordEncoderLoader.getInstance(NoOpPasswordEncoder::getInstance);
    }

    /**
     * 处理用户名和密码校验方法
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        UserDetails userDetails = new User("admin", "123456", Collections.emptyList());
//        auth.inMemoryAuthentication()
//                .withUser(userDetails)
//                .passwordEncoder(passwordEncoder());
        auth.userDetailsService(jdbcUserDetailsService).passwordEncoder(passwordEncoder());
    }
}
