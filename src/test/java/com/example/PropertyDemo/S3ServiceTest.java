package com.example.PropertyDemo;

import com.amazonaws.services.s3.AmazonS3;
import com.example.PropertyDemo.Services.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
public class S3ServiceTest {

    @Configuration
    static class Config {

        @Bean
        public S3Service s3Service() {
            return new S3Service();
        }

    }

    @MockBean
    AmazonS3 s3;

    @Autowired
    S3Service s3Service;

    @Test
    public void canUploadFile() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        URL url = new URL("https://url");
        String filename = "property_image_1_1";
        when(s3.getUrl(eq(S3Service.S3_BUCKET_NAME), eq(filename))).thenReturn(url);

        URL returnedURL = s3Service.save(multipartFile, filename);

        assertThat(returnedURL).isEqualTo(url);
        verify(multipartFile).transferTo(any(Path.class));

    }
}
