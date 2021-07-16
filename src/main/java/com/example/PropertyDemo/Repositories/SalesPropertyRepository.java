package com.example.PropertyDemo.Repositories;

import com.example.PropertyDemo.Property.SaleProperty;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesPropertyRepository extends PropertyBaseRepository<SaleProperty> {
    List<SalesPropertyRepository> findByPrice(double price);
}
