package com.example.PropertyServer.Repositories;

import com.example.PropertyServer.Property.SaleProperty;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalePropertyRepository extends PropertyBaseRepository<SaleProperty> {
    List<SalePropertyRepository> findByPrice(double price);
}
