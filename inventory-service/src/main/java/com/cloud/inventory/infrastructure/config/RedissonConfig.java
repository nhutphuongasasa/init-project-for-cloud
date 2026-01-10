package com.cloud.inventory.infrastructure.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() throws IOException {
        ClassPathResource resource = new ClassPathResource("redisson.yml");
        try (InputStream is = resource.getInputStream()) {
            Config config = Config.fromYAML(is);
            return Redisson.create(config);
        }
    }
}