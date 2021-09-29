package com.example.PropertyDemo.SpecificationBuilders;

import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.PropertyType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpecificationBuilder<T extends Property> {


    public Specification<T> build(Map<String, String> searchParams) {
        if(searchParams.isEmpty())
            return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        else {
            List<Specification<T>> specs;

            specs = searchParams.keySet().stream()
                    .map(key -> getSpec(key, searchParams.get(key)))
                    .collect(Collectors.toList());

            Specification<T> finalSpec = specs.get(0);
            for (Specification<T> nextSpec : specs.subList(1, specs.size())) {
                finalSpec = finalSpec.and(nextSpec);
            }
            return finalSpec;
        }

    }

    protected Specification<T> getSpec(String field, String value) {
        Specification<T> spec;
        switch(field) {
            case "city":
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("location").get("city"), value);
                break;
            case "postcode":
                spec =  (root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("location").get("postCode"), value + "%");
                break;
            case "type":
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"),
                        PropertyType.valueOf(value));
                break;
            case "min":
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("bedrooms"), value);
                break;
            case "max":
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("bedrooms"), value);
                break;
            default:
                throw new IllegalArgumentException("Filter on \"" + field + "\" field not supported");
        }
        return spec;
    }


}
