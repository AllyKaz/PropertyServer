package com.example.PropertyServer.Services;

import com.example.PropertyServer.Agent.Agent;
import com.example.PropertyServer.AgentNotFoundException;
import com.example.PropertyServer.Property.Property;
import com.example.PropertyServer.Repositories.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AgentService {

    @Autowired
    AgentRepository agentRepository;

    @Autowired
    PropertyService propertyService;

    @Autowired
    S3Service s3Service;

    public Agent createAgent(Agent agent, MultipartFile logo) throws IOException {
        agentRepository.save(agent);
        agent.setLogoImage(s3Service.save(logo, "agent_" + agent.getId() + "_logo"));
        return agentRepository.save(agent);
    }

    public List<Agent> getAll() {
        return agentRepository.findAll();
    }

    public Agent getAgent(int id) {
        return agentRepository.findById(id).orElseThrow(() -> new AgentNotFoundException(id));
    }

    public List<Property> getAgentProperties(int id) { return agentRepository.findById(id)
            .orElseThrow(() -> new AgentNotFoundException(id)).getProperties();}

    public Agent getAgentForProperty(int property_id) {
        return propertyService.getProperty(property_id).getAgent();
    }
}

