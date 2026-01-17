// package com.cloud.auth_service.infrastructure.security.components;

// import java.io.IOException;
// import java.util.Map;

// import org.springframework.http.MediaType;
// import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
// import org.springframework.http.server.ServletServerHttpResponse;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
// import org.springframework.stereotype.Component;

// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class OAuth2TokenSuccessHandler implements AuthenticationSuccessHandler{
//     private final MappingJackson2HttpMessageConverter jsonConverter;

//     @Override
//     public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//         Authentication authentication) throws IOException, ServletException {
//         log.info("Chặng cuối: Đổi mã code thành công, đang chuẩn bị trả JWT cho UI...");
    
//         if(authentication instanceof OAuth2AccessTokenAuthenticationToken oAuth2Token){
//             String accessToken = oAuth2Token.getAccessToken().getTokenValue();

//             Map<String, String> tokenResponse = Map.of(
//                 "access_token", accessToken,
//                 "token_type", "Bearer",
//                 "status", "success",
//                 "expires_in", "3600"
//             );

//             jsonConverter.write(tokenResponse, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));

//             log.info("JWT Token đã được gửi về UI thành công.");
//         }

//         log.error("Error type in OAuth2TokenSuccessHandler");
//     }
// }
