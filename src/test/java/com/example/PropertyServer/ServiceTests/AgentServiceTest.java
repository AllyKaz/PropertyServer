package com.example.PropertyServer.ServiceTests;

import com.example.PropertyServer.Agent.Agent;
import com.example.PropertyServer.Property.Property;
import com.example.PropertyServer.Property.RentalProperty;
import com.example.PropertyServer.Property.SaleProperty;
import com.example.PropertyServer.Repositories.AgentRepository;
import com.example.PropertyServer.Services.AgentService;
import com.example.PropertyServer.Services.S3Service;
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

import static com.example.PropertyServer.TestUtils.PropertyServerTestHelper.generateId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
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
        when(s3Service.save(eq(logo), anyString())).thenReturn(url);

        Agent returnedAgent = agentService.createAgent(agent, logo);

        assertThat(returnedAgent, equalTo(agent));

        verify(agent, times(1)).setLogoImage(eq(url));

    }

    @Test
    public void getAllAgents() {
        List<Agent> agents = Arrays.asList(mock(Agent.class), mock(Agent.class), mock(Agent.class));
        when(agentRepository.findAll()).thenReturn(agents);
        List<Agent> returnedAgents = agentService.getAll();
        assertThat(returnedAgents, equalTo(agents));
    }

    @Test
    public void getAgentById() {
        Agent agent = mock(Agent.class);
        int ID = generateId();
        when(agentRepository.findById(eq(ID))).thenReturn(Optional.of(agent));
        Agent returnedAgent = agentService.getAgent(ID);
        assertThat(returnedAgent, equalTo(agent));
    }

    @Test
    public void getPropertiesByAgent() {
        Agent agent = mock(Agent.class);
        final int AGENT_ID = 1;
        RentalProperty rentalProperty = mock(RentalProperty.class);
        SaleProperty saleProperty = mock(SaleProperty.class);
        when(agentRepository.findById(AGENT_ID)).thenReturn(Optional.ofNullable(agent));
        when(agent.getProperties()).thenReturn(Arrays.asList(rentalProperty, saleProperty));

        List<Property> properties = agentService.getAgentProperties(AGENT_ID);

        assertThat(properties, contains(rentalProperty, saleProperty));
    }
}
