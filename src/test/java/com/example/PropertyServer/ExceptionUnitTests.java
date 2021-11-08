package com.example.PropertyServer;

import com.example.PropertyServer.Exceptions.AgentNotFoundException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ExceptionUnitTests {

    @Test
    public void AgentNotFoundHasCorrectMessage() {
        int AGENT_ID = 1;
        AgentNotFoundException ex = new AgentNotFoundException(AGENT_ID);
        assertThat(ex.getMessage(), equalTo("Agent with id = " + AGENT_ID + " not found."));
    }

}
