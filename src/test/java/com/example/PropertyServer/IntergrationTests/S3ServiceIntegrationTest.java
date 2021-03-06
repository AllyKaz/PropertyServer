package com.example.PropertyServer.IntergrationTests;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.PropertyServer.Services.S3Service;
import com.example.PropertyServer.TestUtils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

@SpringJUnitConfig
public class S3ServiceIntegrationTest {

    @Configuration
    @PropertySource("classpath:/application.properties")
    public static class Config {

        @Value("${s3.bucket.name}")
        String bucketname;

        @Bean
        public AmazonS3 getAmazonS3Client() {return AmazonS3ClientBuilder.defaultClient();}

        @Bean
        public S3Service getS3Service() {return new S3Service(bucketname);}

    }

    @Autowired
    S3Service s3Service;

    final String S3_BUCKET_NAME;

    final String AMAZON_S3_BUCKET_URL_REGEX;

    public S3ServiceIntegrationTest(@Value("${s3.bucket.name}") String bucketname) {
        this.S3_BUCKET_NAME = bucketname;
        this.AMAZON_S3_BUCKET_URL_REGEX = "https://" + this.S3_BUCKET_NAME + "\\.s3\\.eu-west-2\\.amazonaws\\.com/";
    }

    @Test
    public void canUploadImage() throws IOException {
        MultipartFile image = TestUtils.createImageMultipart(1).get(0);
        URL url = s3Service.save(image, "filename");
        assertThat(url.toExternalForm(), matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "filename"));
    }
}
