package com.example.PropertyDemo.Repositories;

import com.example.PropertyDemo.Property.RentalProperty;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalPropertyRepository extends PropertyBaseRepository<RentalProperty> {

    List<RentalProperty> findByMonthlyRent(double monthlyRent);

}
