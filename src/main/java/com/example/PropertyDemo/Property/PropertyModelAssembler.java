package com.example.PropertyDemo.Property;

import com.example.PropertyDemo.Controllers.PropertyController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PropertyModelAssembler implements RepresentationModelAssembler<Property, EntityModel<Property>> {

    @Override
    public EntityModel<Property> toModel(Property property) {
        EntityModel<Property> entityModel = EntityModel.of(property,
                linkTo(methodOn(PropertyController.class).getProperty(property.getId())).withSelfRel(),
                linkTo(methodOn(PropertyController.class)
                        .getAllProperties(null, null, null, null, null))
                        .withRel("properties"));
        return entityModel;
    }

}
