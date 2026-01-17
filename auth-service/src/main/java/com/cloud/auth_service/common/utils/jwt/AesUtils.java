package com.cloud.auth_service.common.utils.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

/**
 * @nhutphuong
 * @since 2026/1/11 16:30h
 * @version 1
 */
@Component
public class AesUtils {

    public String encrypt(String data, String secret) throws Exception {
        //tao key cho aes
        byte[] keyBytes = fixSecret(secret);
        SecretKeySpec spec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, spec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public String decrypt(String encryptedData, String secret) throws Exception {
        byte[] keyBytes = fixSecret(secret);
        SecretKeySpec spec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, spec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
    }

    private byte[] fixSecret(String secret) {
        byte[] key = new byte[16];
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(secretBytes, 0, key, 0, Math.min(secretBytes.length, 16));
        return key;
    }
}