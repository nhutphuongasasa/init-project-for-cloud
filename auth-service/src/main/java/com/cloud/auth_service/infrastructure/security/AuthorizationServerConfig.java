package com.cloud.auth_service.infrastructure.security;

import com.cloud.auth_service.domain.model.Role;
import com.cloud.auth_service.domain.model.User;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.UserRepository;
import com.cloud.auth_service.infrastructure.config.properties.AppProperties;
import com.cloud.auth_service.infrastructure.exception.UserNotFoundException;
import com.cloud.auth_service.infrastructure.security.components.CustomOidcUserService;
import com.cloud.auth_service.infrastructure.security.components.LoginSuccessHandler;
// import com.cloud.auth_service.infrastructure.security.components.OAuth2TokenSuccessHandler;
import com.cloud.auth_service.infrastructure.security.keys.JwtManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author nhutphuong
 * @version 3
 * @since 2026/1/17 17:56
1. Tại sao chưa kịp chọn Account Google đã hiện Popup?
Đây là cái bẫy "Redirect 2 lần" (Double Redirect) mà trình duyệt thực hiện cực nhanh:

Bước 1: Bạn bấm Login. Trình duyệt gọi UAA qua cổng 8000.

Bước 2: UAA nhận request. Nó kiểm tra và thấy: "À, anh này muốn sang Google". Nó trả về mã 302 Redirect kèm địa chỉ Google.

Bước 3 (Quan trọng nhất): Vì UAA chạy ở 8005 nhưng request đến từ 8000, nếu không có cái ForwardedHeaderFilter kia, UAA sẽ tạo ra một cái link Redirect sai port hoặc sai giao thức (ví dụ nó bắt trình duyệt quay lại 8005 thay vì 8000).

Bước 4: Trình duyệt thấy sự mâu thuẫn này (Security Violation) ngay lập tức. Trước khi nó kịp load trang Google, Spring Security đã chặn đứng lại vì nghi ngờ tấn công. Và vì nó chặn lại ở tầng Security, nó tung cái Popup ra để "hỏi tội" bạn.

2. Tại sao "Có Session rồi" vẫn hiện Popup?
Đây là lý do tại sao cái Ordered.HIGHEST_PRECEDENCE của bạn lại quan trọng đến thế:

Security Filter Chain của Spring giống như một dàn lính gác.

Nếu bạn không có cái Filter kia chạy trước (HIGHEST_PRECEDENCE), gói tin đi vào đến lính gác vẫn mang nhãn "Tôi đến từ port 8005".

Lính gác nhìn vào Session (đang ghi port 8000) và nhìn vào gói tin (đang ghi port 8005) -> Không khớp.

Dù Session của bạn là thật, nhưng vì thông tin Port bị lệch, lính gác coi Session đó không hợp lệ (Invalid Session).

Khi Session bị coi là không hợp lệ -> Bạn trở thành "người lạ" -> Popup hiện ra.

3. Tại sao cái Bean ForwardedHeaderFilter lại giải quyết được?
Cái Filter này nó đứng trước cửa toàn bộ hệ thống (trước cả Security):

Nó nhặt gói tin từ Gateway (8000) gửi xuống.

Nó thấy header X-Forwarded-Host: 8000.

Nó "phẫu thuật" gói tin: Nó ghi đè lại toàn bộ thông tin port 8005 thành 8000.

Khi gói tin này đi vào đến các tầng Security bên trong, các lính gác nhìn vào và thấy: "Mọi thứ đều là 8000, khớp hoàn toàn với Session!".

Thế là chúng cho bạn đi qua mượt mà, không hỏi han (Popup) gì nữa.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthorizationServerConfig {

    private final UserRepository userRepository;
    private final LoginSuccessHandler loginSuccessHandler;
    // private final OAuth2TokenSuccessHandler tokenSuccessHandler;
    private final AppProperties appProperties;
    private final JwtManager jwtManager;
    private final CustomOidcUserService customOidcUserService;
    private JWKSource<SecurityContext> jwkSource;

    /*
    buoc phai co cai nay vav phai chanh dau tien de tranh authorization bat loi 
    authorization server khi nam sau 1 proxy ma thay request den ci domain khac ban than 8000 != 8005(port ban than) -> nghi ngo hack -> uppop
    khi dung authorization sau proxy con co session de duy tri phien login  neu khong se gay loi 
    */
    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
        FilterRegistrationBean<ForwardedHeaderFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // Ép chạy trước cả Security
        return registration;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        http.securityMatcher(request -> {
            String path = request.getServletPath();
            return authorizationServerConfigurer.getEndpointsMatcher().matches(request) || 
                path.startsWith("/oauth2/") || 
                path.startsWith("/login") ||           
                path.startsWith("/api/auth/") ||
                path.equals("/error");
        });

        http.formLogin(loginForm -> loginForm.disable());
        http.httpBasic(basic -> basic.disable());
        http.csrf(csrf -> csrf.disable());


        http.with(authorizationServerConfigurer, (authServer) -> 
            authServer.oidc(Customizer.withDefaults())
        );

        http.oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(ui -> ui.oidcUserService(customOidcUserService))
            .successHandler(loginSuccessHandler)
        );

        http.exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/google"))
        );

        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/.well-known/**", 
                "/login/**", 
                "/api/auth/**", 
                "/error",
                "/oauth2/token"
            ).permitAll()
            .requestMatchers(authorizationServerConfigurer.getEndpointsMatcher()).authenticated() 
            .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    public CommandLineRunner initClients(RegisteredClientRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            saveClientIfNotExists(repository, RegisteredClient.withId("warehouse-ui-id")
                    .clientId("warehouse-client")
                    .clientSecret(passwordEncoder.encode("secret")) 
                    .clientName("Warehouse NextJS UI")
                    .clientAuthenticationMethods(m -> {
                        m.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                        m.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                    })
                    .authorizationGrantTypes(g -> {
                        g.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                        g.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                        g.add(AuthorizationGrantType.REFRESH_TOKEN);
                    })
                    .redirectUri(appProperties.getFrontend().getCallbackUrl())
                    .scope("openid").scope("profile").scope("email")
                    .clientSettings(ClientSettings.builder()
                            .requireProofKey(false)
                            .requireAuthorizationConsent(false)
                            .build())
                    .build());

            List<String> services = List.of("vendor-service", "inventory-service", "order-service", "product-service");
            
            for (String serviceName : services) {
                saveClientIfNotExists(repository, RegisteredClient.withId(serviceName + "-id")
                        .clientId(serviceName)
                        .clientSecret(passwordEncoder.encode(serviceName)) 
                        .clientName(serviceName.toUpperCase())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .scope("read").scope("write")
                        .build());
            }
        };
    }

    private void saveClientIfNotExists(RegisteredClientRepository repository, RegisteredClient client) {
        if (repository.findByClientId(client.getClientId()) == null) {
            repository.save(client);
            log.info("✅ Registered client: {}", client.getClientId());
        }
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                Authentication principal = context.getPrincipal();

                if (principal instanceof OidcUser oidcUser) {
                    String email = oidcUser.getEmail();

                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new UserNotFoundException());

                    context.getClaims().claims(claims -> {
                        claims.put("user_id", user.getId());
                        claims.put("type", "USER");
                        claims.put("email", user.getEmail());
                        claims.put("fullName", user.getFullName());

                        Set<String> roles = user.getRoles().stream()
                                .map(Role::toString)
                                .collect(Collectors.toSet());
                        claims.put("roles", roles);
                    });
                }

                if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType())) {
                    context.getClaims().claims(claims -> {
                        claims.put("type", "SERVICE");
                        claims.put("service_id", context.getRegisteredClient().getClientId());
                        claims.put("service_name", context.getRegisteredClient().getClientName());

                        Set<String> scopes = context.getRegisteredClient().getScopes();
                        claims.put("authorities", scopes);
                    });
                }

                context.getClaims()
                        .issuer(appProperties.getSecurity().getJwtIssuer())
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(appProperties.getSecurity().getAccessTokenValidityInSeconds()));

                try {
                    Map<String, Object> allClaims = context.getClaims().build().getClaims();
                    String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(allClaims);
                    log.info("\n🔍 [JWT DEBUG] FULL CLAIMS:\n{}\n", prettyJson);
                } catch (Exception e) {
                    log.error("❌ Failed to log JWT claims", e);
                }
            }
        };
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        if (this.jwkSource == null) {
            RSAKey rsaKey = jwtManager.getLastRsaKey();
            JWKSet jwkSet = new JWKSet(rsaKey);
            this.jwkSource = new ImmutableJWKSet<>(jwkSet);
        }
        return this.jwkSource;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }
}