package com.example.PropertyServer.Repositories;

import com.example.PropertyServer.Property.RentalProperty;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalPropertyRepository extends PropertyBaseRepository<RentalProperty> {

    List<RentalProperty> findByMonthlyRent(double monthlyRent);

}
