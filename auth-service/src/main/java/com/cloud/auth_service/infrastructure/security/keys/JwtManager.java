package com.cloud.auth_service.infrastructure.security.keys;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.cloud.auth_service.domain.model.JwtKeys;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.JwtKeyRepository;
import com.cloud.auth_service.infrastructure.config.properties.AppProperties;
import com.nimbusds.jose.jwk.RSAKey;

import lombok.RequiredArgsConstructor;

/**
 * @nhutphuong
 * @since 2026/1/11 16h
 * @version 1
 */
@Component
@RequiredArgsConstructor
public class JwtManager {
	private final JwtKeyRepository jwtKeyRepository;
	private final AppProperties appProperties;

    public RSAKey getLastRsaKey(){
		

        return null;
    }

    private RSAKey generateAndSaveKey() throws Exception{
        KeyPair keyPair = generateRsaKey();

		String pubStr = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
		String priStr = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

		String publicKey = parsPrivateKey(priStr).toString();
		String privateKey = parsePublicKey(pubStr).toString();

		String encryptedPrivateKey = "";

        jwtKeyRepository.save(
			JwtKeys.builder()
				.privateKey(publicKey)
				.publicKey(privateKey)
				.algorithm("RSA")
				.build()
		);

        return null;
    }

	private RSAPublicKey parsePublicKey(String keyStr) throws Exception {
		byte[] keyBytes = Base64.getDecoder().decode(keyStr);
		return (RSAPublicKey) KeyFactory.getInstance("RSA")
			.generatePublic(new X509EncodedKeySpec(keyBytes));
	}

	private RSAPrivateKey parsPrivateKey(String keyStr) throws Exception{
		byte[] keyBytes = Base64.getDecoder().decode(keyStr);
		return (RSAPrivateKey) KeyFactory.getInstance("RSA")
			.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
	}

    private KeyPair generateRsaKey() { 
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}
}
