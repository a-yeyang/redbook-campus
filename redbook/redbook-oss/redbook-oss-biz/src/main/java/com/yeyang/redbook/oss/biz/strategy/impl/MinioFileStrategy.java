package com.yeyang.redbook.oss.biz.strategy.impl;

import com.yeyang.redbook.oss.biz.config.MinioProperties;
import com.yeyang.redbook.oss.biz.strategy.FileStrategy;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
public class MinioFileStrategy implements FileStrategy {

    @Resource
    private MinioProperties minioProperties;

    @Resource
    private MinioClient minioClient;

    @Override
    @SneakyThrows
    public String uploadFile(MultipartFile file, String bucketName) {
        if (file == null || file.getSize() == 0) {
            throw new IllegalArgumentException("file must not be empty");
        }

        ensureBucket(bucketName);

        String originalFileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        String suffix = "";
        if (originalFileName != null && originalFileName.lastIndexOf(".") >= 0) {
            suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String objectName = UUID.randomUUID().toString().replace("-", "") + suffix;

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(contentType)
                .build());

        String url = String.format("%s/%s/%s", minioProperties.getEndpoint(), bucketName, objectName);
        log.info("Uploaded file to MinIO: {}", url);
        return url;
    }

    @SneakyThrows
    private void ensureBucket(String bucketName) {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());

        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }
}
