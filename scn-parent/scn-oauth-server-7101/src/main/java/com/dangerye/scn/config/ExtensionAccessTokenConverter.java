package com.dangerye.scn.config;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import java.util.Map;
import java.util.Optional;

@Component
public class ExtensionAccessTokenConverter extends DefaultAccessTokenConverter {
    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        final String clientIp = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes).getRequest())
                .map(ServletRequest::getRemoteAddr)     // todo 此地址 非发起地址 通过代理的话 获取 发起地址需要 额外操作
                .orElse("");
        final Map<String, Object> resultMap = (Map<String, Object>) super.convertAccessToken(token, authentication);
        resultMap.putIfAbsent("clientIp", clientIp);
        return resultMap;
    }
}
