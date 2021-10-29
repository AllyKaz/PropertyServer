package com.example.PropertyServer.IntergrationTests;

import com.example.PropertyServer.AgentNotFoundException;
import com.example.PropertyServer.Property.Property;
import com.example.PropertyServer.Property.RentalProperty;
import com.example.PropertyServer.Property.SaleProperty;
import com.example.PropertyServer.Services.PropertyService;
import com.example.PropertyServer.Services.S3Service;
import com.example.PropertyServer.SpecificationBuilders.RentalPropertySpecificationBuilder;
import com.example.PropertyServer.SpecificationBuilders.SalePropertySpecificationBuilder;
import com.example.PropertyServer.SpecificationBuilders.SpecificationBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@DataJpaTest
public class PropertyServiceIntegrationTests {

    @TestConfiguration
    public static class Config {

        @Bean
        public PropertyService getPropertyService() {
            return new PropertyService();
        }
    }

    @Autowired
    PropertyService propertyService;

    @MockBean
    RentalPropertySpecificationBuilder rentalPropertySpecificationBuilder;

    @MockBean
    SpecificationBuilder<Property> specificationBuilder;

    @MockBean
    SalePropertySpecificationBuilder salePropertySpecificationBuilder;

    @MockBean
    S3Service s3Service;

    @Test
    public void createSalePropertyThrowsAgentNotFoundException() {
        int AGENT_ID = 1;
        SaleProperty saleProperty = mock(SaleProperty.class);
        assertThrows(AgentNotFoundException.class, () -> propertyService.createSaleProperty(saleProperty, AGENT_ID,
                new MultipartFile[]{mock(MultipartFile.class)}));
    }

    @Test
    public void createRentalPropertyThrowsAgentNotFoundException() {
        int AGENT_ID = 1;
        assertThrows(AgentNotFoundException.class, () -> propertyService.createRentalProperty(
                mock(RentalProperty.class), AGENT_ID, new MultipartFile[]{mock(MultipartFile.class)}));
    }

}
