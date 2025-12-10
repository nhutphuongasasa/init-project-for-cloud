package com.cloud.product_service.infrastructure.adapter.inbound.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class S3Controller {

    private final S3Presigner presigner;
    private final String bucketName;

    public S3Controller(
        @Value("${aws.access-key}") String accessKey,
        @Value("${aws.secret-key}") String secretKey,
        @Value("${aws.region}") String region,
        @Value("${aws.s3.bucket}") String bucketName
    ) {
        this.bucketName = bucketName;

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        this.presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    @GetMapping("/presigned-url")
    public Map<String, String> getPresignedUrl(@RequestParam String fileName) {
        String uniqueFileName = UUID.randomUUID() + "-" + fileName;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .build();

        PresignedPutObjectRequest presignedRequest =
                presigner.presignPutObject(r -> r.signatureDuration(Duration.ofMinutes(5))
                        .putObjectRequest(objectRequest));

        return Map.of("url", presignedRequest.url().toString());
    }
}
