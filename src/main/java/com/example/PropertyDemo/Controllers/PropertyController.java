package com.example.PropertyDemo.Controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.PropertyType;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.PropertyBaseRepository;
import com.example.PropertyDemo.Repositories.RentalPropertyRepository;
import com.example.PropertyDemo.Repositories.SalesPropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.PropertyDemo.Controllers.Specifications.*;

@RestController
@RequestMapping("/")
public class PropertyController {

    @Autowired
    private PropertyBaseRepository<Property> propertyRepository;

    @Autowired
    private SalesPropertyRepository salesPropertyRepository;

    @Autowired
    private RentalPropertyRepository rentalPropertyRepository;

    @Autowired
    private AgentRepository agentRepository;

    private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

    private final String S3_BUCKET_NAME = "propertytestbucket";



    @GetMapping("/properties/{id}")
    public Property getProperty(@PathVariable int id) {
        Property property = propertyRepository.findById(id).orElseThrow(() -> new RuntimeException());
        return property;
    }

    @GetMapping("/properties")
    public List<Property> getAllProperties(
            @PathVariable(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postcode,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max) {

        return propertyRepository.findAll(inCity(city).and(inPostCode(postcode))
                .and(isType(type)).and(hasBedrooms(min, max))).stream().collect(Collectors.toList());
    }


    @GetMapping("/rentalProperties")
    public List<Property> getAllRentalProperties(
            @PathVariable(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postcode,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max) {
        return rentalPropertyRepository.findAll(inCity(city).and(inPostCode(postcode))
                .and(isType(type)).and(hasBedrooms(min, max))).stream().collect(Collectors.toList());
    }

    @GetMapping("/salesProperties")
    public List<Property> getAllSalesProperties(
            @PathVariable(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postcode,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max) {
        return salesPropertyRepository.findAll(inCity(city).and(inPostCode(postcode))
                .and(isType(type)).and(hasBedrooms(min, max))).stream().collect(Collectors.toList());
    }


    @GetMapping("/properties/{id}/agent")
    public Agent getAgentForProperty(@PathVariable int id) {
        return propertyRepository.findById(id).get().getAgent();
    }

    @GetMapping("/agents/{id}")
    public Agent getAgent(@PathVariable int id) {
        return agentRepository.findById(id).get();
    }

    @GetMapping("/agents")
    public List<Agent> getAllAgents() {
        List<Agent> agents = agentRepository.findAll();
        return agents;
    }

    @GetMapping("/agents/{id}/properties")
    public Collection<Property> getAgentProperties(@PathVariable int id) {
        List<Property> properties = agentRepository.findById(id).get().getProperties();
        return properties;
    }


    @PostMapping("/agent/{id}/properties/rentals")
    public ResponseEntity<RentalProperty> addRentalPropertyToAgent(@PathVariable int id, @RequestPart RentalProperty property,
                                             @RequestPart(required = false) MultipartFile... images) throws IOException {
        if(images != null) {
            for(MultipartFile image: images) {
                String IMAGE_KEY = id + "_image_" + property.getImages().size();
                PutObjectResult result = s3.putObject(S3_BUCKET_NAME, IMAGE_KEY, image.getInputStream(), new ObjectMetadata());
                property.addImage(s3.getUrl(S3_BUCKET_NAME, IMAGE_KEY));
            }
        }
        Agent agent = agentRepository.findById(id).get();
        agent.addProperty(rentalPropertyRepository.save(property));
        agentRepository.save(agent);
        return new ResponseEntity<RentalProperty>(property, HttpStatus.CREATED);
    }


    @PostMapping("/agent/{id}/properties/sales")
    public Property addRentalPropertyToAgent(@PathVariable int id, @RequestBody SaleProperty property) {
        Agent agent = agentRepository.findById(id).get();
        agent.addProperty(salesPropertyRepository.save(property));
        agentRepository.save(agent);
        return property;
    }

    @PatchMapping("/property/{id}/images")
    public Property addImageToProperty(@PathVariable int id, @RequestPart MultipartFile ...images) throws IOException {
        Property property = propertyRepository.findById(id).get();

        for(MultipartFile image: images) {
            String IMAGE_KEY = id +"_image_" + (property.getImages().size() + 1);
            PutObjectResult result = s3.putObject(S3_BUCKET_NAME, IMAGE_KEY, image.getInputStream(), new ObjectMetadata());
            property.addImage(s3.getUrl(S3_BUCKET_NAME, IMAGE_KEY));
        }
        propertyRepository.save(property);
        return property;
    }

    @PostMapping("/agent")
    public ResponseEntity<Agent> addAgent(@RequestPart Agent agent, @RequestPart MultipartFile logo) throws IOException {
        agentRepository.save(agent);
        String IMAGE_KEY = agent.getId() + "_logo";
        PutObjectResult result = s3.putObject(S3_BUCKET_NAME, IMAGE_KEY, logo.getInputStream(), new ObjectMetadata());
        agent.setLogoImage(s3.getUrl(S3_BUCKET_NAME, IMAGE_KEY));
        return new ResponseEntity<Agent>(agentRepository.save(agent), HttpStatus.CREATED);
    }



}
