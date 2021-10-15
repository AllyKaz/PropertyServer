package com.example.PropertyServer.Builders;

import com.example.PropertyServer.Agent.Agent;
import com.example.PropertyServer.Location.Location;
import com.example.PropertyServer.Property.PropertyType;

import java.net.URL;
import java.util.List;

public abstract class PropertyBuilderAdapter<T extends PropertyBuilderAdapter<T>> implements PropertyBuilder<T> {


    protected PropertyType type;
    protected Location location;
    protected int bedrooms;
    protected Agent agent;
    protected List<URL> images;

    @Override
    public T withType(PropertyType type) {
        this.type = type;
        return getThis();
    }

    @Override
    public T withLocation(Location location) {
        this.location = location;
        return getThis();
    }

    @Override
    public T withBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
        return getThis();
    }

    @Override
    public T withAgent(Agent agent) {
        this.agent = agent;
        return getThis();
    }

    @Override
    public T withImages(List<URL> images) {
        this.images = images;
        return getThis();
    }


    @SuppressWarnings("unchecked")
    private T getThis() {
        return (T) this;
    }


}
