package com.example.PropertyDemo.Controllers;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Agent.AgentModelAssembler;
import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.PropertyModelAssembler;
import com.example.PropertyDemo.Property.PropertyType;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.PropertyBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.PropertyDemo.Controllers.Specifications.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/")
public class PropertyController {

    @Autowired
    private PropertyBaseRepository<Property> propertyRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private PropertyModelAssembler propertyModelAssembler;

    @Autowired
    private AgentModelAssembler agentModelAssembler;


    @GetMapping("/")
    public RepresentationModel<?> root() {
        RepresentationModel<?> rootResource = new RepresentationModel<>();

        rootResource.add(
                linkTo(methodOn(PropertyController.class).root()).withSelfRel(),
                linkTo(methodOn(PropertyController.class)
                        .getAllProperties(null, null, null,null, null)).withRel("properties")
        );

        return rootResource;
    }




    @GetMapping("/properties/{id}")
    public EntityModel<Property> getProperty(@PathVariable int id) {
        Property property = propertyRepository.findById(id).orElseThrow(() -> new RuntimeException());
        return propertyModelAssembler.toModel(property);
    }


    @GetMapping("/agents/{id}")
    public EntityModel<Agent> getAgent(@PathVariable int id) {
        return agentModelAssembler.toModel(agentRepository.findById(id).get());
    }

    @GetMapping("/properties")
    public CollectionModel<EntityModel<Property>> getAllProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postcode,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max) {
            List<EntityModel<Property>> properties = propertyRepository.findAll(inCity(city).and(inPostCode(postcode))
                    .and(isType(type)).and(hasBedrooms(min, max))).stream()
                    .map(propertyModelAssembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(properties, linkTo(methodOn(PropertyController.class)
                .getAllProperties(city, postcode, type, min, max)).withSelfRel());
    }

    @GetMapping("/properties/{id}/agent")
    public EntityModel<Agent> getAgentForProperty(@PathVariable int id) {
        return agentModelAssembler.toModel(propertyRepository.findById(id).get().getAgent());
    }


    @GetMapping("/agents")
    public CollectionModel<EntityModel<Agent>> getAllAgents() {
        List<EntityModel<Agent>> agents = agentRepository.findAll().stream()
                .map(agentModelAssembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(agents, linkTo(methodOn(PropertyController.class).getAllAgents()).withSelfRel());
    }

    @GetMapping("/agents/{id}/properties")
    public CollectionModel<EntityModel<Property>> getAgentProperties(@PathVariable int id) {
        List<EntityModel<Property>> properties = agentRepository.findById(id).get().getProperties().stream()
                .map(propertyModelAssembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(properties, linkTo(methodOn(PropertyController.class).getAgentProperties(id))
                .withSelfRel());
    }

    @PostMapping("/properties")
    public ResponseEntity<?> addProperty(@RequestBody RentalProperty property) {
        return propertyModelAssembler.toModel(propertyRepository.save(property)).getLink(IanaLinkRelations.SELF)
                .map(Link::getHref).map(href -> {
                    try {
                        return new URI(href);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }).map(uri -> ResponseEntity.noContent().location(uri).build())
                .orElse(ResponseEntity.badRequest().body("Unable to create Resource"));
    }

    @PostMapping("/agent/{id}/properties/rentals")
    public ResponseEntity<?> addPropertyToAgent(@PathVariable int id, @RequestBody RentalProperty property) {
        Agent agent = agentRepository.findById(id).get();
        agent.addProperty(propertyRepository.save(property));
        agentRepository.save(agent);
        return propertyModelAssembler.toModel(property).getLink(IanaLinkRelations.SELF)
                .map(Link::getHref).map(href -> {
                    try {
                        return new URI(href);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }).map(uri -> ResponseEntity.noContent().location(uri).build())
                .orElse(ResponseEntity.badRequest().body("Unable to add property"));
    }




}
