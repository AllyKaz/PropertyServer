package com.example.PropertyDemo.Controllers;

import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.PropertyType;
import com.example.PropertyDemo.Property.PropertyModelAssembler;
import com.example.PropertyDemo.Repositories.RentalPropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.PropertyDemo.Controllers.Specifications.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController("/rentals")
public class RentalPropertyController {

    @Autowired
    RentalPropertyRepository rentalPropertyRepository;

    @Autowired
    PropertyModelAssembler propertyModelAssembler;

    @GetMapping("")
    public CollectionModel<EntityModel<Property>> getAllRentalProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postcode,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max) {
        List<EntityModel<Property>> properties = rentalPropertyRepository.findAll(inCity(city).and(inPostCode(postcode))
                .and(isType(type)).and(hasBedrooms(min, max))).stream()
                .map(propertyModelAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(properties, linkTo(methodOn(RentalPropertyController.class)
                .getAllRentalProperties(city, postcode, type, min, max)).withSelfRel());
    }
}
