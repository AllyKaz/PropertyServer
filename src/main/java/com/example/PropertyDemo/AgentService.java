package com.example.PropertyDemo;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Repositories.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class AgentService {

    @Autowired
    AgentRepository agentRepository;

    @Autowired
    S3Service s3Service;

    public Agent createAgent(Agent agent, MultipartFile logo) throws IOException {
        agent.setLogoImage(s3Service.save(logo));
        return agentRepository.save(agent);
    }

    public List<Agent> getAll() {
        return agentRepository.findAll();
    }

    public Agent getAgent(int id) {
        return agentRepository.findById(id).get();
    }
}

