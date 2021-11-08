package com.example.PropertyServer.TestUtils;

import com.example.PropertyServer.Agent.Agent;
import com.example.PropertyServer.Property.Property;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TestUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static MockMultipartFile buildPropertyMultiPart(Property property) throws JsonProcessingException {
        return new MockMultipartFile("property", "property", "application/json",
                mapper.writeValueAsString(property).getBytes());
    }

    public static MockMultipartFile buildAgentMultiPart(Agent agent) throws JsonProcessingException {
        return new MockMultipartFile("agent", "agent", "application/json",
                mapper.writeValueAsString(agent).getBytes());
    }

    public static MockMultipartFile buildImageMultiPart() {
        return new MockMultipartFile("images", "image", "image/jpeg", "image".getBytes());
    }

    public static MockMultipartFile buildLogoMultiPart() {
        return new MockMultipartFile("logo", "logo", "image/jpeg", "logo".getBytes());
    }

    public static List<MockMultipartFile> createImageMultipart(int size) {
        List<MockMultipartFile> files = new ArrayList<>();
        IntStream.range(0, size).forEach(i -> {
            String filename = "test_image_" + (i + 1) + ".jpeg";
            try {
                Path path = new ClassPathResource(filename).getFile().toPath();
                files.add(new MockMultipartFile("images", filename, "image/jpg",
                        Files.readAllBytes(path)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return files;
    }
}
