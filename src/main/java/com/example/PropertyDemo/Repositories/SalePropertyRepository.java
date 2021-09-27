package com.example.PropertyDemo.Repositories;

import com.example.PropertyDemo.Property.SaleProperty;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalePropertyRepository extends PropertyBaseRepository<SaleProperty> {
    List<SalePropertyRepository> findByPrice(double price);
}
