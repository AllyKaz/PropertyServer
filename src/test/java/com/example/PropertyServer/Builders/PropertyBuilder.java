package com.example.PropertyServer.Builders;

import com.example.PropertyServer.Agent.Agent;
import com.example.PropertyServer.Location.Location;
import com.example.PropertyServer.Property.PropertyType;

import java.net.URL;
import java.util.List;

public interface PropertyBuilder<T extends PropertyBuilder<T>> {

    T withType(PropertyType type);
    T withLocation(Location location);
    T withBedrooms(int bedrooms);
    T withAgent(Agent agent);
    T withImages(List<URL> images);


}
