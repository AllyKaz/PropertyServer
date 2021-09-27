package com.example.PropertyDemo.Builders;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Location;
import com.example.PropertyDemo.Property.PropertyType;

import java.net.URL;
import java.util.List;

public interface PropertyBuilder<T extends PropertyBuilder<T>> {

    T withType(PropertyType type);
    T withLocation(Location location);
    T withBedrooms(int bedrooms);
    T withAgent(Agent agent);
    T withImages(List<URL> images);



}
