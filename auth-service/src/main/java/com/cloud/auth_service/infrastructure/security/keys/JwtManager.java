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

import com.cloud.auth_service.common.utils.jwt.AesUtils;
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
	private final AesUtils aesUtils;

	public RSAKey getLastRsaKey() {
		try {
			JwtKeys jwtKey = jwtKeyRepository.findTopByOrderByCreatedAtDesc()
				.orElse(null);
		
			if(jwtKey == null){
				return generateAndSaveKey();
			}	

			String privateKey = aesUtils.decrypt(jwtKey.getPrivateKey(), appProperties.getAesKey());
			String publicKey = jwtKey.getPublicKey();

			//phai  parse tu base64  vi khi luu vao da base64
			return new RSAKey.Builder(parsePublicKey(publicKey))
				.privateKey(parsPrivateKey(privateKey))
				.keyID(jwtKey.getKid().toString())
				.build();

		} catch (Exception e) {
			e.printStackTrace(); 
        	throw new RuntimeException("Không thể khởi tạo JWK Source: " + e.getMessage());
		}
	}

    public RSAKey generateAndSaveKey() throws Exception{
        KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    	RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		//base64 vi rsa la mang byte
		String pubStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
		String priStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());

		String encryptedPrivateKey = aesUtils.encrypt(priStr, appProperties.getAesKey());

        JwtKeys jwtKey =jwtKeyRepository.save(
			JwtKeys.builder()
				.privateKey(encryptedPrivateKey)
				.publicKey(pubStr)
				.algorithm("RSA")
				.build()
		);

		return new RSAKey.Builder(publicKey)
			.privateKey(privateKey)
			.keyID(jwtKey.getKid().toString())
			.build();

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
