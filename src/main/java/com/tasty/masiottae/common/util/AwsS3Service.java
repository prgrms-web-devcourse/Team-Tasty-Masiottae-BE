package com.tasty.masiottae.common.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.tasty.masiottae.common.exception.ErrorMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private static String getExtension(String originalFileName) {
        int extensionIndex = originalFileName.lastIndexOf(".");
        if (extensionIndex == -1) {
            return "";
        }
        return originalFileName.substring(extensionIndex);
    }

    public String uploadMenuImage(MultipartFile multipartFile) {
        return upload(multipartFile, ImageDirectory.MENU);
    }

    public String uploadFranchiseImage(MultipartFile multipartFile) {
        return upload(multipartFile, ImageDirectory.FRANCHISE);
    }

    public String upload(MultipartFile multipartFile, ImageDirectory imageDirectory) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        InputStream inputStream = getInputStream(multipartFile);
        String fileName = buildFileName(multipartFile.getOriginalFilename(), imageDirectory);

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream,
                objectMetadata).withCannedAcl(
                CannedAccessControlList.PublicRead));

        return s3Client.getUrl(bucketName, fileName).toString();
    }

    private InputStream getInputStream(MultipartFile multipartFile) {
        try {
            return multipartFile.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessage.IMAGE_SAVE_ERROR.getMessage());
        }
    }

    private String buildFileName(String originalFileName, ImageDirectory imageDirectory) {
        String extension = getExtension(originalFileName);
        return imageDirectory.getS3Directory() + UUID.randomUUID() + extension;
    }
}
