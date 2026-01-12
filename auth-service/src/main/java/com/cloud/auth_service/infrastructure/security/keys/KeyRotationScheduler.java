package com.cloud.auth_service.infrastructure.security.keys;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphunog
 * @since 2026/1/12 18:49h
 * @version 1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeyRotationScheduler {
    private final JwtManager jwtManager;

    @Scheduled(cron = "${nhutphuongasasa.key-rotation-cron}")
    public void rotationRSAKey(){
        log.info("Bắt đầu quá trình xoay vòng khóa RSA tự động...");
        try {
            jwtManager.generateAndSaveKey();
            log.info("Xoay vòng khóa RSA thành công!");
        } catch (Exception e) {
            log.error("Lỗi nghiêm trọng khi xoay vòng khóa RSA: {}", e.getMessage());
        }
    }
}
