package com.example.PropertyServer.Services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class S3Service {

    @Autowired
    AmazonS3 s3;

    public static final String S3_BUCKET_NAME = "propertyserverbucket";


    public URL save(MultipartFile multipartFile, String filename) throws IOException {
        Path tempFile = Files.createTempFile("temp", "tmp");
        multipartFile.transferTo(tempFile);
        PutObjectResult putObjectResult =  s3.putObject(S3_BUCKET_NAME, filename, tempFile.toFile());
        return s3.getUrl(S3_BUCKET_NAME, filename);
    }
}
