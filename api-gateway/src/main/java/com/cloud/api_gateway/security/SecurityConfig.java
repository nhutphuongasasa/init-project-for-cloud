package com.cloud.api_gateway.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private List<String> permitAllPaths = List.of(
        "/actuator/**", 
        "/login-custom",
        "/welcome",
        "/redis",
        "/internal/keycloak/event"
    );

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange
                    .pathMatchers(
                        "/internal/keycloak/event/**", 
                        "/swagger-ui/**", 
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/vendor/v3/api-docs",
                        "/inventory/v3/api-docs",
                        "/product/v3/api-docs",
                        "/order/v3/api-docs",
                        "/webjars/**",
                        "/swagger-resources/**"
                    ).permitAll()
                    .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .pathMatchers(permitAllPaths.toArray(String[]::new)).permitAll()
                    .anyExchange().authenticated()
                )
                .httpBasic(basic -> basic.disable())
                .csrf(csrf -> csrf.disable())
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

}
