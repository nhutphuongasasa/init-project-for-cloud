package com.cloud.auth_service.infrastructure.security.components;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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

@Override
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    Authentication authentication) throws IOException, ServletException {
    
    log.info("Chặng 1: Login Google thành công.");

    this.setDefaultTargetUrl(appProperties.getFrontend().getCallbackUrl());

    log.info("Xử lý thành công. Spring sẽ tự động kiểm tra SavedRequest để cấp Auth Code.");

    super.onAuthenticationSuccess(request, response, authentication);
}
}
