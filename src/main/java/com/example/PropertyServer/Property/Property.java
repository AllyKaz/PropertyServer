package com.example.PropertyServer.Property;


import com.example.PropertyServer.Agent.Agent;
import com.example.PropertyServer.Location.Location;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Inheritance
public abstract class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull(message = "property type must not be null")
    private PropertyType type;

    @NotNull(message = "location must not be null")
    @Valid
    private Location location;

    @Min(value = 1, message = "property must have at least one bedroom")
    private int bedrooms;

    @ElementCollection
    private List<URL> images = new ArrayList<>();

    @JsonIgnore
    @ManyToOne
    private Agent agent;

    public Property() {
    }

    public Property(PropertyType type, Location location, int bedrooms, List<URL> images, Agent agent) {
        this.type = type;
        this.location = location;
        this.bedrooms = bedrooms;
        this.images = images;
        this.agent = agent;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public List<URL> getImages() {
        return images;
    }

    public void setImages(List<URL> images) {
        this.images = images;
    }

    public void addImage(URL image) {
        this.images.add(image);
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return bedrooms == property.bedrooms && type == property.type && location.equals(property.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, location, bedrooms);
    }
}
