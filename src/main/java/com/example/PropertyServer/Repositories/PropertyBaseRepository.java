package com.example.PropertyServer.Repositories;

import com.example.PropertyServer.Property.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PropertyBaseRepository<T extends Property> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {
    T findByAgentId(int i);
}
