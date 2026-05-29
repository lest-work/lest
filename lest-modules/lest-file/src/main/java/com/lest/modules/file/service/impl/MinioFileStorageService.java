package com.lest.modules.file.service.impl;

import com.lest.modules.file.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileStorageService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public void upload(String objectName, InputStream inputStream, long size, String contentType) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucket()).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucket()).build());
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            log.debug("文件上传成功: {}", objectName);
        } catch (Exception e) {
            log.error("文件上传失败: {}", objectName, e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    public void delete(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(objectName)
                            .build()
            );
            log.debug("文件删除成功: {}", objectName);
        } catch (Exception e) {
            log.error("文件删除失败: {}", objectName, e);
            throw new RuntimeException("文件删除失败: " + e.getMessage());
        }
    }

    public InputStream get(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("文件读取失败: {}", objectName, e);
            throw new RuntimeException("文件读取失败: " + e.getMessage());
        }
    }

    public String getPresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucket())
                            .object(objectName)
                            .expiry(minioConfig.getPresignedUrlExpiry(), TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}", objectName, e);
            throw new RuntimeException("生成预签名URL失败: " + e.getMessage());
        }
    }

    public String getPresignedUrlForDownload(String objectName, String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucket())
                            .object(objectName)
                            .expiry(minioConfig.getPresignedUrlExpiry(), TimeUnit.SECONDS)
                            .extraQueryParams(Map.of("Response-content-disposition", "attachment;filename=\"" + fileName + "\""))
                            .build()
            );
        } catch (Exception e) {
            log.error("生成下载URL失败: {}", objectName, e);
            throw new RuntimeException("生成下载URL失败: " + e.getMessage());
        }
    }
}
