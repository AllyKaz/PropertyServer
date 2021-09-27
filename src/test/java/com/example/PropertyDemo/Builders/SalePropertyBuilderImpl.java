package com.example.PropertyDemo.Builders;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Location;
import com.example.PropertyDemo.Property.PropertyType;
import com.example.PropertyDemo.Property.SaleProperty;

import java.net.URL;
import java.util.List;

public class SalePropertyBuilderImpl implements SalePropertyBuilder<SalePropertyBuilderImpl> {

    private PropertyType type;
    private Location location;
    private int bedrooms;
    private Agent agent;
    private List<URL> images;
    private double price;

    @Override
    public SalePropertyBuilderImpl withType(PropertyType type) {
        this.type = type;
        return this;
    }

    @Override
    public SalePropertyBuilderImpl withLocation(Location location) {
        this.location = location;
        return this;
    }

    @Override
    public SalePropertyBuilderImpl withBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
        return this;
    }

    @Override
    public SalePropertyBuilderImpl withAgent(Agent agent) {
        this.agent = agent;
        return this;
    }

    @Override
    public SalePropertyBuilderImpl withImages(List<URL> images) {
        this.images = images;
        return this;
    }

    @Override
    public SalePropertyBuilderImpl withPrice(double price) {
        this.price = price;
        return this;
    }

    @Override
    public SaleProperty build() {
        return new SaleProperty(type, location, bedrooms, images, agent, price);
    }
}
