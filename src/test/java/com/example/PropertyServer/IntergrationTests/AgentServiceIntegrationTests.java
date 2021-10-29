package com.example.PropertyServer.IntergrationTests;

import com.example.PropertyServer.AgentNotFoundException;
import com.example.PropertyServer.Services.AgentService;
import com.example.PropertyServer.Services.S3Service;
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
    }

    @Autowired
    AgentService agentService;

    @MockBean
    S3Service s3Service;

    @Test
    public void getAgentThrowsAgentNotFoundException() {
        assertThrows(AgentNotFoundException.class, () -> agentService.getAgent(1));
    }

    @Test
    public void getAgentPropertiesThrowsAgentNotFoundException() {
        assertThrows(AgentNotFoundException.class, () -> agentService.getAgentProperties(1));
    }

}
