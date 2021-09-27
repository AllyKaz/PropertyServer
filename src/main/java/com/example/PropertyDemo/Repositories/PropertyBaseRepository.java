package com.example.PropertyDemo.Repositories;

import com.example.PropertyDemo.Property.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PropertyBaseRepository<T extends Property> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {
    T findByAgentId(int i);
}
