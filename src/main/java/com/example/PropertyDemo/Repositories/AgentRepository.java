package com.example.PropertyDemo.Repositories;

import com.example.PropertyDemo.Agent.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Integer> {
}
