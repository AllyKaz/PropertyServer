package com.example.PropertyServer.SpecificationBuilders;

import com.example.PropertyServer.Property.SaleProperty;
import org.springframework.data.jpa.domain.Specification;

public class SalePropertySpecificationBuilder extends SpecificationBuilder<SaleProperty> {

    @Override
    protected Specification<SaleProperty> getSpec(String field, String value) {
        switch (field) {
            case "minPrice":
                return ((root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), Integer.parseInt(value)));
            case "maxPrice":
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), Integer.parseInt(value));
            default:
                return super.getSpec(field, value);
        }
    }
}
