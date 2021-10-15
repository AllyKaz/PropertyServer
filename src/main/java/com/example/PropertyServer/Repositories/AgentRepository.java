package com.example.PropertyServer.Repositories;

import com.example.PropertyServer.Agent.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Integer> {
}
