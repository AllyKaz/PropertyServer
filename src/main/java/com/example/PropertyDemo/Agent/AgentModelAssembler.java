package com.example.PropertyDemo.Agent;

import com.example.PropertyDemo.Controllers.PropertyController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AgentModelAssembler implements RepresentationModelAssembler<Agent, EntityModel<Agent>> {

    @Override
    public EntityModel<Agent> toModel(Agent agent) {
        EntityModel<Agent> entityModel = EntityModel.of(agent,
                linkTo(methodOn(PropertyController.class).getAgent(agent.getId())).withSelfRel(),
                linkTo(methodOn(PropertyController.class).getAllAgents()).withRel("agents"));
        return entityModel;
    }
}
