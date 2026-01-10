package com.cloud.auth_service.common.utils.jwt;
// package com.cloud.authentication_service.common.utils.jwt;

// import java.time.Instant;
// import java.util.List;
// import java.util.Map;
// import java.util.UUID;
// import java.util.stream.Collectors;

// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
// import org.springframework.stereotype.Component;

// /**
//  * @author nhutphuong
//  * @version 1.0
//  * @created 25/11/2025
//  */
// @Component
// public final class JwtUtils {

//     public String getTokenFromContextHolder() {
//         Authentication auth = getAuthentication();
//         if (auth instanceof JwtAuthenticationToken jwtauth) {
//             return jwtauth.getToken().getTokenValue();
//         }
//         return null;
//     }

//     public UUID getCurrentUserId(){
//         Jwt jwt = getJwt();
//         return jwt != null ? UUID.fromString(jwt.getClaim("sub")) : null;
//     }

//     public String getCurrentUsername() {
//         Authentication auth = getAuthentication();
//         return auth != null ? auth.getName() : null;
//     }

//     public String getCurrentFullName() {
//         Jwt jwt = getJwt();
//         return jwt != null ? jwt.getClaim("name") : null;
//     }

//     public List<String> getCurrentRoles() {
//         Authentication auth = getAuthentication();
//         if (auth != null){
//             return auth.getAuthorities()
//                 .stream()
//                 .map(role -> role.getAuthority())
//                 .collect(Collectors.toList());
//         }else{
//             return List.of();
//         }
//     }

//     public Instant getCurrentExpireAt() {
//         Jwt jwt = getJwt();
//         return jwt != null ? jwt.getExpiresAt() : null;
//     }

//     public Instant getCurrentIssueAt() {
//         Jwt jwt = getJwt();
//         return jwt != null ? jwt.getIssuedAt() : null;
//     }

//     public String getCurrentUserEmail() {
//         Jwt jwt = getJwt();
//         return jwt != null ? jwt.getClaim("email") : null;
//     }

//     public Map<String, Object> getCurrentUserClaims() {
//         Jwt jwt = getJwt();
//         return jwt != null ? jwt.getClaims() : null;
//     }

//     private Authentication getAuthentication() {
//         return SecurityContextHolder.getContext().getAuthentication();
//     }

//     private Jwt getJwt() {
//         Authentication auth = getAuthentication();
//         if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
//             return jwt;
//         }
//         return null;
//     }
// }
