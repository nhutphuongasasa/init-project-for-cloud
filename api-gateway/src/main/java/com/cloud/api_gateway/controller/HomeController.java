package com.cloud.api_gateway.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.cloud.api_gateway.common.color.DefaultColor;
// import com.cloud.api_gateway.utils.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Value;

import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeController {
    @Value("${redirect.vendor.url}")
    private String redirectVendorUrl;
    @Value("${redirect.admin.url}")
    private String redirectAdminUrl;
    private final ReactiveOAuth2AuthorizedClientService authorizedClientService;

    
    @GetMapping("/okela")
    public Mono<Void> home(ServerWebExchange exchange, @AuthenticationPrincipal OAuth2User user) {
        if (user != null) {
            System.out.println(DefaultColor.YELLOW + user);
            String sessionId = exchange.getRequest()
                                   .getCookies()
                                   .getFirst("SESSION") != null
                                   ? exchange.getRequest().getCookies().getFirst("SESSION").getValue()
                                   : "no-session";

        System.out.println("=== SESSION COOKIE ===");
        System.out.println(sessionId);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND);

        String role = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse("VENDOR");


        String redirectUrl;

        if ("ROLE_ADMIN".equals(role) || "ADMIN".equals(role)) {
            redirectUrl = redirectAdminUrl;
        } else {
            redirectUrl = redirectVendorUrl;
        }
        response.getHeaders().setLocation(URI.create(redirectUrl));
        return response.setComplete();
        }
        return Mono.empty();
        
    }

    @GetMapping("/oauth2user")
    public Mono<Map<String, Object>> printOAuth2User(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return Mono.just(Map.of("error", "Chưa login"));
        }

        Map<String, Object> result = new HashMap<>(user.getAttributes());
        result.put("authorities", user.getAuthorities());
        result.put("name", user.getName());

        log.info("OAuth2User attributes: {}", user.getAttributes());
        log.info("OAuth2User authorities: {}", user.getAuthorities());

        return Mono.just(result);
    }

    @GetMapping("/")
    public Mono<String> token(OAuth2AuthenticationToken authentication) {
        return authorizedClientService
            .loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
            )
            .map(OAuth2AuthorizedClient::getAccessToken)
            .map(token -> {
                System.out.println("=== ACCESS TOKEN ===");
                System.out.println("Token Value: " + token.getTokenValue());
                System.out.println("Issued At: " + token.getIssuedAt());
                System.out.println("Expires At: " + token.getExpiresAt());
                
                return token.getTokenValue();
            });
    }

    // @GetMapping("/redis")
    // public Mono<String> redis() {
    //     return redisUtils.setValue("okela", "Hello World")
    //                 .doOnSuccess(success -> System.out.println("set okela"))
    //                 .then(redisUtils.getValue("okela"))
    //                 .doOnNext(value -> System.out.println("get okela: " + value))
    //                 .map(value -> "redis value = " + value)
    //                 .defaultIfEmpty("default value");
    // }

    @PostMapping("/internal/keycloak/event")
    public Mono<String> sendEvent() {
        System.out.println("=== SỰ KIỆN ĐÃ ĐƯỢC GỬI ===");
        return Mono.just("Event sent successfully");
    }
}