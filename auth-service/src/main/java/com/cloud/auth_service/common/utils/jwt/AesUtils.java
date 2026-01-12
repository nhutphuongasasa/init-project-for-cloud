package com.cloud.auth_service.common.utils.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @nhutphuong
 * @since 2026/1/11 16:30h
 * @version 1
 */
public class AesUtils {

    public String encrypt(String data, String secret) throws Exception {
        //tao key cho aes
        SecretKeySpec spec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, spec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public String decrypt(String encryptedData, String secret) throws Exception {
        SecretKeySpec spec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, spec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
    }
}