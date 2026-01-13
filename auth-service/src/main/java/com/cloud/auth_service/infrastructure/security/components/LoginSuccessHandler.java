package com.cloud.auth_service.infrastructure.security.components;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.cloud.auth_service.application.service.UserService;
import com.cloud.auth_service.infrastructure.config.properties.AppProperties;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @since 2026-01-010 11:47
 * @version 1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
    private final AppProperties appProperties;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        
        log.info("Chặng 1: Login Google thành công, đang xử lý lưu User...");
        
        if(authentication instanceof OAuth2AuthenticationToken oauthToken){
            String provider = oauthToken.getAuthorizedClientRegistrationId();

            Object principal = oauthToken.getPrincipal();

            if(principal instanceof OidcUser oidcUser){
                userService.syncUser(oidcUser, provider);
            }
        }
        this.setDefaultTargetUrl(appProperties.getFrontend().getCallbackUrl());
        this.setAlwaysUseDefaultTargetUrl(true); 

        log.info("Login thành công. Đang đẩy User về trang xử lý code: {}", appProperties.getFrontend().getCallbackUrl());

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
