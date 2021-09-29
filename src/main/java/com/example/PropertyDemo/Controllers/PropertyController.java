package com.example.PropertyDemo.Controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Services.AgentService;
import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import com.example.PropertyDemo.Services.PropertyService;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.PropertyBaseRepository;
import com.example.PropertyDemo.Repositories.RentalPropertyRepository;
import com.example.PropertyDemo.Repositories.SalePropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class PropertyController {

    @Autowired
    private PropertyBaseRepository<Property> propertyRepository;

    @Autowired
    private SalePropertyRepository salePropertyRepository;

    @Autowired
    private RentalPropertyRepository rentalPropertyRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AgentService agentService;

    private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

    private final String S3_BUCKET_NAME = "propertytestbucket";




    @GetMapping("/properties")
    public List<Property> getAllProperties(@RequestParam Map<String, String> searchParameters) {
        return propertyService.getAllProperties(searchParameters);
    }

    @GetMapping("/properties/{id}")
    public Property getProperty(@PathVariable int id) {
        return propertyService.getProperty(id);
    }

    @GetMapping("/properties/{id}/agent")
    public Agent getAgentForProperty(@PathVariable int id) {
        return propertyRepository.findById(id).get().getAgent();
    }

    @PatchMapping("/properties/{id}/images")
    public Property addImageToProperty(@PathVariable int id, @RequestPart MultipartFile ...images) throws IOException {
        Property property = propertyRepository.findById(id).get();

        for(MultipartFile image: images) {
            String IMAGE_KEY = id +"_property_image_" + (property.getImages().size());
            PutObjectResult result = s3.putObject(S3_BUCKET_NAME, IMAGE_KEY, image.getInputStream(), new ObjectMetadata());
            property.addImage(s3.getUrl(S3_BUCKET_NAME, IMAGE_KEY));
        }
        propertyRepository.save(property);
        return property;
    }


    @GetMapping("/properties/rentals")
    public List<RentalProperty> getAllRentalProperties(@RequestParam Map<String, String> params) {
        return propertyService.getAllRentalProperties(params);
    }

    @GetMapping("/properties/sales")
    public List<SaleProperty> getAllSalesProperties(@RequestParam Map<String, String> params) {
            return propertyService.getAllSaleProperties(params);
    }

    @GetMapping("/agents")
    public List<Agent> getAllAgents() {
        return agentService.getAll();
    }

    @PostMapping("/agents")
    public ResponseEntity<Agent> addAgent(@RequestPart Agent agent, @RequestPart MultipartFile logo) throws IOException {
        return new ResponseEntity<Agent>(agentService.createAgent(agent, logo), HttpStatus.CREATED);
    }

    @GetMapping("/agents/{id}")
    public Agent getAgent(@PathVariable int id) {
        return agentRepository.findById(id).get();
    }



    @GetMapping("/agents/{id}/properties")
    public Collection<Property> getAgentProperties(@PathVariable int id) {
        List<Property> properties = agentRepository.findById(id).get().getProperties();
        return properties;
    }


    @PostMapping("/agents/{id}/properties/rentals")
    public ResponseEntity<RentalProperty> addRentalPropertyToAgent(@PathVariable int id, @RequestPart @Valid RentalProperty property,
                                             @RequestPart MultipartFile... images) {
        RentalProperty persistedProperty = null;
        try {
            persistedProperty = propertyService.createRentalProperty(property, id, images);
            return new ResponseEntity<RentalProperty>(persistedProperty, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/agents/{id}/properties/sales")
    public ResponseEntity<SaleProperty> addSalesPropertyToAgent(@PathVariable int id, @RequestPart SaleProperty property,
            @RequestPart MultipartFile... images)  {
        SaleProperty newProperty = null;
        try {
            newProperty = propertyService.createSaleProperty(property, id, images);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<SaleProperty>(newProperty, HttpStatus.CREATED);
    }









}
