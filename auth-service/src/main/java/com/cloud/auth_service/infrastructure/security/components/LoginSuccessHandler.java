package com.cloud.auth_service.infrastructure.security.components;

import java.io.IOException;
import java.time.Instant;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.cloud.auth_service.domain.model.User;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.UserRepository;
import com.cloud.auth_service.infrastructure.config.properties.AppProperties;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @since 2026-01-010 11:47
 * 
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
    private final AppProperties appProperties;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        
        log.info("Chặng 1: Login Google thành công, đang xử lý lưu User...");
        
        if(authentication instanceof OAuth2AuthenticationToken oauthToken){
            String provider = oauthToken.getAuthorizedClientRegistrationId();

            Object principal = oauthToken.getPrincipal();

            if(principal instanceof OidcUser oidcUser){
                String email = oidcUser.getEmail();
                String providerId = oidcUser.getAttribute("sub");
                String fullName = oidcUser.getFullName();
                String avatarUrl = oidcUser.getAttribute("picture");

                User existedUser = userRepository.findByEmail(email)
                    .orElse(null);

                if(existedUser == null){
                    User newUser = User.builder()
                        .provider(provider)
                        .providerId(providerId)
                        .email(email)
                        .fullName(fullName)
                        .avatarUrl(avatarUrl)
                        .emailVerified(true)
                        .lastLogin(Instant.now())
                        .build();

                    userRepository.save(newUser);
                } else{
                    existedUser.setLastLogin(Instant.now());
                    userRepository.save(existedUser);
                }

                log.info("Đã lưu user mới: {}", email);
            }
        }
        this.setDefaultTargetUrl(appProperties.getFrontend().getCallbackUrl());
        this.setAlwaysUseDefaultTargetUrl(true); 

        log.info("Login thành công. Đang đẩy User về trang xử lý code: {}", appProperties.getFrontend().getCallbackUrl());

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
