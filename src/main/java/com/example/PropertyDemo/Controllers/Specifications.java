package com.example.PropertyDemo.Controllers;

import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.PropertyType;
import org.springframework.data.jpa.domain.Specification;

public class Specifications {

    public static Specification<Property> inCity(String city) {
        if(city == null)
            return alwaysTrue();
        else
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("location").get("city"), city);
    }

    public static Specification<Property> inPostCode(String postCode) {
        if(postCode == null)
            return alwaysTrue();
        else
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("location").get("postCode"), postCode + "%");
    }

    public static Specification<Property> isType(PropertyType type) {
        return (type == null)? alwaysTrue():
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), type);

    }

    public static Specification<Property> hasBedrooms(Integer min, Integer max) {
        if(max == null && min != null)
            return (root, query, criteriaBuilder) -> criteriaBuilder.gt(root.get("bedrooms"), min);
        if(max != null && min == null)
            return (root, query, criteriaBuilder) -> criteriaBuilder.lt(root.get("bedrooms"), max);
        if(max != null && min != null)
            return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("bedrooms"), min, max);
        else
            return alwaysTrue();
    }

    public static Specification<Property> alwaysTrue() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(criteriaBuilder.literal(true));
    }
}
