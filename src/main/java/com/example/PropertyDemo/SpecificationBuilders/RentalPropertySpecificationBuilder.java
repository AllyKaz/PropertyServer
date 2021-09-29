package com.example.PropertyDemo.SpecificationBuilders;

import com.example.PropertyDemo.Property.RentalProperty;
import org.springframework.data.jpa.domain.Specification;

public class RentalPropertySpecificationBuilder extends SpecificationBuilder<RentalProperty> {

    @Override
    protected Specification<RentalProperty> getSpec(String field, String value) {
        switch(field) {
            case "minMonthlyRent":
                return (root, query, criteriaBuilder) -> criteriaBuilder
                        .greaterThanOrEqualTo(root.get("monthlyRent"), Integer.parseInt(value));
            case "maxMonthlyRent":
                return ((root, query, criteriaBuilder) -> criteriaBuilder
                        .lessThanOrEqualTo(root.get("monthlyRent"), Integer.parseInt(value)));
            default:
                return super.getSpec(field, value);
        }
    }
}
