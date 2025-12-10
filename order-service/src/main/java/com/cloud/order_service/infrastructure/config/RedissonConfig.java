package com.cloud.order_service.infrastructure.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class RedissonConfig {

    // @Bean
    // public RedissonClient redissonClient() throws FileNotFoundException, IOException {
    //     File file = ResourceUtils.getFile("classpath:redisson.yml");
    //     Config config = Config.fromYAML(file);
    //     return Redisson.create(config);
    // }

    @Bean
public RedissonClient redissonClient() throws IOException {
    ClassPathResource resource = new ClassPathResource("redisson.yml");
    try (InputStream is = resource.getInputStream()) {
        Config config = Config.fromYAML(is);
        return Redisson.create(config);
    }
}

}