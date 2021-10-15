package com.example.PropertyServer.Controllers;

import com.example.PropertyServer.Property.Property;
import com.example.PropertyServer.Property.PropertyType;
import org.springframework.data.jpa.domain.Specification;

public class Specifications {

    public static Specification<? extends Property> inCity(String city) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("location").get("city"), city);
    }

    public static Specification<Property> inPostCode(String postCode) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("location").get("postCode"), postCode + "%");
    }

    public static Specification<Property> isType(PropertyType type) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Property> hasMinBedrooms(int min) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("bedrooms"), min);
    }

    public static Specification<Property> hasMaxBedrooms(int max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("bedrooms"), max);
    }

}
