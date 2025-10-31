package com.cloud.api_gateway.config.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        "/welcome"
    );

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange
                    .pathMatchers(permitAllPaths.toArray(String[]::new)).permitAll()
                    .anyExchange().authenticated()
                )
                .httpBasic(basic -> basic.disable())
                .csrf(csrf -> csrf.disable())
                .oauth2Login(Customizer.withDefaults())
                // .oauth2Client(Customizer.withDefaults())
                .build();
    }
}
