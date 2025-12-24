package com.cloud.api_gateway.security;

import java.net.URI;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;

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
                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessHandler(logoutSuccessHandler())   // dùng handler reactive
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

    @Bean
    public RedirectServerLogoutSuccessHandler logoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
        handler.setLogoutSuccessUrl(URI.create("http://localhost:3000"));
        return handler;
    }
}
