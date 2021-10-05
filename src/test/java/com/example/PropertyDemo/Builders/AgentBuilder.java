package com.example.PropertyDemo.Builders;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Location.Location;

import java.net.URL;

public class AgentBuilder {
    private String name;
    private Location location;
    private URL logo;
    private String telephoneNumber;

    public AgentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AgentBuilder withLocation(Location location) {
        this.location = location;
        return this;
    }

    public AgentBuilder withLogo(URL logo) {
        this.logo = logo;
        return this;
    }

    public AgentBuilder withTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
        return this;
    }

    public Agent build() {
        return new Agent(name, location, telephoneNumber, logo);
    }

    public static AgentBuilder getInstance() {
        return new AgentBuilder();
    }



}
