package com.example.PropertyServer.TestUtils;

import com.example.PropertyServer.Property.Property;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockMultipartFile;

public class TestUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static MockMultipartFile buildPropertyMultiPart(Property property) throws JsonProcessingException {
        return new MockMultipartFile("property", "property", "application/json",
                mapper.writeValueAsString(property).getBytes());
    }

    public static MockMultipartFile buildImageMultiPart() {
        return new MockMultipartFile("images", "image", "image/jpeg", "image".getBytes());
    }
}
