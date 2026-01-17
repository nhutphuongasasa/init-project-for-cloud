// package com.cloud.api_gateway.security.filters;

// import org.springframework.cloud.gateway.filter.GatewayFilterChain;
// import org.springframework.cloud.gateway.filter.GlobalFilter;
// import org.springframework.core.Ordered;
// import org.springframework.security.core.context.ReactiveSecurityContextHolder;
// import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
// import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;

// import lombok.extern.slf4j.Slf4j;
// import reactor.core.publisher.Mono;

// /**
//  * @author: Phuong Le
//  * @date: 2025-11-22
//  * description: them accesstoken vao header
// */

// @Component
// @Slf4j
// public class RealTokenRelayFilter implements GlobalFilter, Ordered {

//     private final ReactiveOAuth2AuthorizedClientService clientService;

//     public RealTokenRelayFilter(ReactiveOAuth2AuthorizedClientService clientService) {
//         this.clientService = clientService;
//     }

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//         return ReactiveSecurityContextHolder.getContext()
//             .filter(ctx -> ctx.getAuthentication() instanceof OAuth2AuthenticationToken)
//             .flatMap(ctx -> {
//                 OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) ctx.getAuthentication();
//                 String clientId = auth.getAuthorizedClientRegistrationId();
//                 String principalName = auth.getName();

//                 return clientService.loadAuthorizedClient(clientId, principalName)
//                     .flatMap(client -> {
//                         String accessToken = client.getAccessToken().getTokenValue();
//                         log.info("RELAY ACCESS TOKEN (có role + group): {}...", 
//                                 accessToken.substring(0, 30));

//                         ServerWebExchange mutated = exchange.mutate()
//                             .request(r -> r.header("Authorization", "Bearer " + accessToken))
//                             .build();
//                         return chain.filter(mutated);
//                     });
//             })
//             .switchIfEmpty(chain.filter(exchange))
//             .then();
//     }

//     @Override
//     public int getOrder() {
//         return Ordered.HIGHEST_PRECEDENCE;
//     }
// }

