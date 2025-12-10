// package com.cloud.api_gateway.security.route;


// import com.cloud.api_gateway.common.color.DefaultColor;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
// import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
// import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
// import org.springframework.cloud.gateway.route.RouteLocator;
// import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class GatewayConfig {

//     private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);

//     @Bean
//     public DiscoveryClientRouteDefinitionLocator discoveryClientRouteLocator(
//             ReactiveDiscoveryClient discoveryClient,
//             DiscoveryLocatorProperties properties) {
//         properties.setLowerCaseServiceId(true);
//         properties.setEnabled(true);

//         log.debug(DefaultColor.RED + 
//                   "DiscoveryClientRouteDefinitionLocator enabled with lowerCaseServiceId=" 
//                   + properties.isLowerCaseServiceId() + 
//                   DefaultColor.RESET);

//         return new DiscoveryClientRouteDefinitionLocator(discoveryClient, properties);
//     }

//     @Bean
//     public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//         log.debug(DefaultColor.RED + "Building custom routes for Gateway..." + DefaultColor.RESET);

//         return builder.routes()
//             .route("vendor-service", r -> {
//                 log.debug(DefaultColor.RED + "Registering route for vendor-service at /vendor/**" + DefaultColor.RESET);
//                 return r.path("/vendor/**")
//                         .filters(f -> f.stripPrefix(1))
//                         .uri("lb://vendor-service");
//             })
//             .route("inventory-service", r -> {
//                 log.debug(DefaultColor.RED + "Registering route for inventory-service at /inventory/**" + DefaultColor.RESET);
//                 return r.path("/inventory/**")
//                         .filters(f -> f.stripPrefix(1).preserveHostHeader())
//                         .uri("lb://inventory-service");
//             })
//             .route("product-service", r -> {
//                 log.debug(DefaultColor.RED + "Registering route for product-service at /product/**" + DefaultColor.RESET);
//                 return r.path("/product/**")
//                         .filters(f -> f.preserveHostHeader())
//                         .uri("lb://product-service");
//             })
//             .route("order-service", r -> {
//                 log.debug(DefaultColor.RED + "Registering route for order-service at /order/**" + DefaultColor.RESET);
//                 return r.path("/order/**")
//                         .filters(f -> f.preserveHostHeader())
//                         .uri("lb://order-service");
//             })
//             .build();
//     }
// }
