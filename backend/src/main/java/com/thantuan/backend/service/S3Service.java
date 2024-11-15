package com.thantuan.backend.service;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;
    @Value("${spring.cloud.aws.region.static}")
    private String REGION;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String BUCKET_NAME;

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() +
                Objects.requireNonNull(file.getOriginalFilename())
                        .substring(file.getOriginalFilename()
                                .lastIndexOf("."));
        ObjectMetadata metadata = ObjectMetadata.builder()
                .contentType(file.getContentType())
                .build();
        s3Template.upload(BUCKET_NAME, fileName, file.getInputStream(), metadata);
        return String.format("https://%s.s3.%s.amazonaws.com/%s", BUCKET_NAME, REGION, fileName);
    }

    public void deleteFile(String fileName) {
        s3Template.deleteObject(BUCKET_NAME, fileName);
    }
}