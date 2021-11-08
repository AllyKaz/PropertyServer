package com.example.PropertyServer.IntergrationTests;

import com.example.PropertyServer.Exceptions.AgentNotFoundException;
import com.example.PropertyServer.Property.Property;
import com.example.PropertyServer.Exceptions.PropertyNotFoundException;
import com.example.PropertyServer.Services.AgentService;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class AgentServiceIntegrationTests {

    @TestConfiguration
    static class Config {
        @Bean
        public AgentService agentService() {
            return new AgentService();
        }

        @Bean
        public PropertyService propertyService() { return new PropertyService();}
    }

    @Autowired
    AgentService agentService;

    @MockBean
    S3Service s3Service;

    @Autowired
    PropertyService propertyService;

    @MockBean
    SpecificationBuilder<Property> specificationBuilder;

    @MockBean
    RentalPropertySpecificationBuilder rentalPropertySpecificationBuilder;

    @MockBean
    SalePropertySpecificationBuilder salePropertySpecificationBuilder;

    @Test
    public void getAgentThrowsAgentNotFoundException() {
        assertThrows(AgentNotFoundException.class, () -> agentService.getAgent(1));
    }

    @Test
    public void getAgentPropertiesThrowsAgentNotFoundException() {
        assertThrows(AgentNotFoundException.class, () -> agentService.getAgentProperties(1));
    }

    @Test
    public void getAgentForPropertyThrowsPropertyNotFoundException() {
        assertThrows(PropertyNotFoundException.class, () -> agentService.getAgentForProperty(1));
    }

}
