package com.tasty.masiottae.common.aws;

import static com.tasty.masiottae.common.aws.ImageFileNameResolver.getFullFileName;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.tasty.masiottae.common.exception.ErrorMessage;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class AwsS3ImageUploader {

    private final AmazonS3 s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadMenuImage(MultipartFile multipartFile) {
        return upload(multipartFile, ImageDirectory.MENU);
    }

    public String uploadFranchiseImage(MultipartFile multipartFile) {
        return upload(multipartFile, ImageDirectory.FRANCHISE);
    }

    public String uploadAccountImage(MultipartFile multipartFile) {
        return upload(multipartFile, ImageDirectory.ACCOUNT);
    }

    public String upload(MultipartFile multipartFile, ImageDirectory imageDirectory) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        InputStream inputStream = getInputStream(multipartFile);
        String fileName = getFullFileName(multipartFile.getOriginalFilename(), imageDirectory);

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
}
