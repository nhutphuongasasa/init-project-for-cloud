package com.cloud.api_gateway.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HomeController {

    @GetMapping("/")
    public Mono<String> home(@AuthenticationPrincipal OAuth2User user) {
        if (user != null) {
            return Mono.just("Welcome, " + user.getAttribute("name"));
        }
        return Mono.just("Welcome, anonymous user");
    }
}
