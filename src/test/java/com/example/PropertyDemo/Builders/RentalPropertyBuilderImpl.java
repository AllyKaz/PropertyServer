package com.example.PropertyDemo.Builders;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Location;
import com.example.PropertyDemo.Property.PropertyType;
import com.example.PropertyDemo.Property.RentalProperty;

import java.net.URL;
import java.util.List;

public class RentalPropertyBuilderImpl implements RentalPropertyBuilder<RentalPropertyBuilderImpl> {

    private PropertyType type;
    private Location location;
    private int bedrooms;
    private Agent agent;
    private List<URL> images;
    private int monthlyRent;


    @Override
    public RentalPropertyBuilderImpl withType(PropertyType type) {
        this.type = type;
        return this;
    }

    @Override
    public RentalPropertyBuilderImpl withLocation(Location location) {
        this.location = location;
        return this;
    }

    @Override
    public RentalPropertyBuilderImpl withBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
        return this;
    }

    @Override
    public RentalPropertyBuilderImpl withAgent(Agent agent) {
        this.agent = agent;
        return this;
    }

    @Override
    public RentalPropertyBuilderImpl withImages(List<URL> images) {
        this.images = images;
        return this;
    }

    @Override
    public RentalPropertyBuilderImpl withMonthlyRent(int monthlyRent) {
        this.monthlyRent = monthlyRent;
        return this;
    }

    public RentalProperty build() {
        return new RentalProperty(type, location,bedrooms, images, agent, monthlyRent);
    }
}
