package com.audtream.server.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStorageService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void init() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());

                String policy = """
                    {
                      "Version": "2012-10-17",
                      "Statement": [
                        {
                          "Effect": "Allow",
                          "Principal": "*",
                          "Action": ["s3:GetObject"],
                          "Resource": ["arn:aws:s3:::%s/*"]
                        }
                      ]
                    }
                    """.formatted(bucketName);

                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policy)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MinIO bucket", e);
        }
    }

    public String uploadAudioFile(MultipartFile file, String fileName) throws Exception {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            throw new IllegalArgumentException("Invalid audio file type");
        }

        String uniqueFileName = generateFileName(fileName, contentType);
        String objectName = "audio/" + uniqueFileName;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(contentType)
                        .build()
        );

        return getFileUrl(objectName);
    }

    public String uploadCoverImage(MultipartFile file, String fileName) throws Exception {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid image file type");
        }

        String uniqueFileName = generateFileName(fileName, contentType);
        String objectName = "covers/" + uniqueFileName;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(contentType)
                        .build()
        );

        return getFileUrl(objectName);
    }

    public String getFileUrl(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(60 * 60 * 24 * 7) // 7 dni
                        .build()
        );
    }

    public InputStream getFile(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    public void deleteFile(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    public List<String> listFiles(String prefix) throws Exception {
        List<String> files = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .build()
        );

        for (Result<Item> result : results) {
            Item item = result.get();
            files.add(item.objectName());
        }

        return files;
    }

    private String generateFileName(String originalName, String contentType) {
        String extension = getFileExtension(contentType);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int)(Math.random() * 1000));

        return timestamp + "_" + random + extension;
    }

    private String getFileExtension(String contentType) {
        return switch (contentType) {
            case "audio/mpeg" -> ".mp3";
            case "audio/wav" -> ".wav";
            case "audio/flac" -> ".flac";
            case "audio/aac" -> ".aac";
            case "audio/ogg" -> ".ogg";
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".bin";
        };
    }
    public String getPresignedUrl(String objectName, int expirySeconds) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(expirySeconds)
                        .build()
        );
    }

    public String uploadFile(MultipartFile file, String folder, String customName) throws Exception {
        String uniqueFileName = customName != null ? customName : generateFileName(file.getOriginalFilename(), file.getContentType());
        String objectName = folder + "/" + uniqueFileName;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        return getFileUrl(objectName);
    }

    public boolean fileExists(String objectName) throws Exception {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw e;
        }
    }

    public long getFileSize(String objectName) throws Exception {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        ).size();
    }
}
