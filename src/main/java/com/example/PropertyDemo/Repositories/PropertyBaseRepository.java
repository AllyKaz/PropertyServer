package com.example.PropertyDemo.Repositories;

import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PropertyBaseRepository<T extends Property> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<Property> {
    List<T> findByLocationCity(String city);

    List<T> findByBedrooms(int bedrooms);

    List<T> findByType(PropertyType type);

    List<T> findByBedroomsBetween(int min, int max);

    List<T> findByBedroomsGreaterThan(int min);

    List<T> findByLocationPostCode(String postCode);

    List<T> findByLocationPostCodeContains(String part);

    List<T> findByLocationPostCodeStartsWith(String postCode);

    List<T> findByTypeIn(Collection<PropertyType> propertyTypes);
    T findByAgentId(int i);
}
