package com.cloud.auth_service.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import com.cloud.auth_service.domain.model.Role;
import com.cloud.auth_service.domain.model.User;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.UserRepository;
import com.cloud.auth_service.infrastructure.config.properties.AppProperties;
import com.cloud.auth_service.infrastructure.exception.UserNotFoundException;
import com.cloud.auth_service.infrastructure.security.components.LoginSuccessHandler;
import com.cloud.auth_service.infrastructure.security.components.OAuth2TokenSuccessHandler;
import com.cloud.auth_service.infrastructure.security.keys.JwtManager;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import lombok.RequiredArgsConstructor;

/**
 * @author nhutphuong
 * @since 2026-01-09 20:06
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthorizationServerConfig {
	private final UserRepository userRepository;
	private final LoginSuccessHandler loginSuccessHandler;
	private final OAuth2TokenSuccessHandler tokenSuccessHandler;
	private final AppProperties appProperties;
	private final JwtManager jwtManager;
	private JWKSource<SecurityContext> jwkSource;

	//kiem tra url co url trong authorization config 
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
		    OAuth2AuthorizationServerConfigurer.authorizationServer();
			
        authorizationServerConfigurer.oidc(Customizer.withDefaults());

		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
			.tokenEndpoint(tokenEndpoint -> tokenEndpoint
				.accessTokenResponseHandler(tokenSuccessHandler)
			);	

        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
            .with(authorizationServerConfigurer, Customizer.withDefaults());

		return http.build();
    }

	//kiem tra url co "/api" 
	@Bean
    @Order(2)
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder(jwkSource())))
            )
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
				.successHandler(loginSuccessHandler)
			);
        return http.build();
    }

    //loal client id
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
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
			}
		};
	}

	//nha cung cap khoa khong phai nguoi quyet dinh
	@Bean 
	public JWKSource<SecurityContext> jwkSource() {
		if(this.jwkSource == null){
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
