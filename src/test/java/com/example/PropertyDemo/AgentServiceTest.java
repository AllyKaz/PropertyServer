package com.example.PropertyDemo;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Repositories.AgentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.PropertyDemo.TestUtils.PropertyDemoTestHelper.generateId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
public class AgentServiceTest {

    @Configuration
    static class config {

        @Bean
        public AgentService agentService() {
            return new AgentService();
        }
    }

    @MockBean
    AgentRepository agentRepository;

    @MockBean
    S3Service s3Service;

    @Autowired
    AgentService agentService;


    @Test
    public void createNewAgent() throws IOException {
        Agent agent = mock(Agent.class);
        MultipartFile logo = mock(MultipartFile.class);

        when(agentRepository.save(agent)).thenReturn(agent);
        URL url = new URL("https://url");
        when(s3Service.save(logo)).thenReturn(url);

        Agent returnedAgent = agentService.createAgent(agent, logo);

        assertThat(returnedAgent).isEqualTo(agent);

        verify(agent, times(1)).setLogoImage(eq(url));

    }

    @Test
    public void getAllAgents() {
        List<Agent> agents = Arrays.asList(mock(Agent.class), mock(Agent.class), mock(Agent.class));
        when(agentRepository.findAll()).thenReturn(agents);
        List<Agent> returnedAgents = agentService.getAll();
        assertThat(returnedAgents).containsAll(agents);
    }

    @Test
    public void getAgentById() {
        Agent agent = mock(Agent.class);
        int ID = generateId();
        when(agentRepository.findById(eq(ID))).thenReturn(Optional.of(agent));
        Agent returnedAgent = agentService.getAgent(ID);
        assertThat(returnedAgent).isEqualTo(agent);
    }
}
