package com.cloud.vendor_service.infrastructure.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author: nhutphuong
 * @date: 2025-11-24
 * @version: 1.0
 * @description: config security cho vendor service
 */
@EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic.disable())
            .csrf(csrf -> csrf.disable())
            // .oauth2Client(Customizer.withDefaults())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter());
            }));
        return http.build();
    }

    @Bean
public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
    scopesConverter.setAuthorityPrefix("SCOPE_");  // giữ lại để lấy scope nếu cần
    scopesConverter.setAuthoritiesClaimName("scope");

    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
        var authorities = new HashSet<GrantedAuthority>();

        // 1. Lấy từ scope (nếu có)
        authorities.addAll(scopesConverter.convert(jwt));

        // 2. Lấy Realm Roles (đây là phần bạn cần)
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            Collection<String> realmRoles = (Collection<String>) realmAccess.get("roles");
            realmRoles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }

        return authorities;
    });

    return converter;
}
}


