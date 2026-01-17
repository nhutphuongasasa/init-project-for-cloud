package com.cloud.auth_service.infrastructure.security.components;

import com.cloud.auth_service.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Lấy thông tin user từ Google
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            String provider = userRequest.getClientRegistration().getRegistrationId();
            log.info("🔐 Đang thực hiện Sync User từ provider: {}", provider);
            log.info("📧 Email: {}", oidcUser.getEmail());
            
            // Sync user vào database
            userService.syncUser(oidcUser, provider);
            
            log.info("✅ Sync user thành công cho: {}", oidcUser.getEmail());
            
        } catch (Exception e) {
            log.error("❌ Lỗi khi sync user vào Database: {}", e.getMessage(), e);
            // Không throw exception để OAuth flow vẫn tiếp tục
            // User vẫn được authenticate nhưng chưa lưu vào DB
        }

        return oidcUser;
    }
}