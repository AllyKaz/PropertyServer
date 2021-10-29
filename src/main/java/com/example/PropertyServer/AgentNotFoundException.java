package com.example.PropertyServer;

public class AgentNotFoundException extends RuntimeException {

    public AgentNotFoundException(int agent_id) {
        super("Agent with id = " + agent_id + " not found.");
    }
}
