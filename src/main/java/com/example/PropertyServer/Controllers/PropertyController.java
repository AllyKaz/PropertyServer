package com.example.PropertyServer.Controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.PropertyServer.Agent.Agent;
import com.example.PropertyServer.ApiError;
import com.example.PropertyServer.Property.Property;
import com.example.PropertyServer.Property.RentalProperty;
import com.example.PropertyServer.Property.SaleProperty;
import com.example.PropertyServer.PropertyNotFoundException;
import com.example.PropertyServer.Services.AgentService;
import com.example.PropertyServer.Services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class PropertyController {

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
    public ResponseEntity<Property> getProperty(@PathVariable int id) throws EntityNotFoundException {
            Property property = propertyService.getProperty(id);
            return new ResponseEntity<Property>(property, HttpStatus.CREATED);
    }

    @GetMapping("/properties/{id}/agent")
    public Agent getAgentForProperty(@PathVariable int id) {
        return agentService.getAgentForProperty(id);
    }

    @PatchMapping("/properties/{id}/images")
    public Property addImageToProperty(@PathVariable int id, @RequestPart MultipartFile ...images) throws IOException {
        return propertyService.addImagesToProperty(id, images);
    }

    @GetMapping("/rentalProperties")
    public List<RentalProperty> getAllRentalProperties(@RequestParam Map<String, String> params) {
        return propertyService.getAllRentalProperties(params);
    }

    @GetMapping("/saleProperties")
    public List<SaleProperty> getAllSalesProperties(@RequestParam Map<String, String> params) {
            return propertyService.getAllSaleProperties(params);
    }

    @GetMapping("/agents")
    public List<Agent> getAllAgents() {
        return agentService.getAll();
    }

    @PostMapping("/agents")
    public ResponseEntity<Agent> addAgent(@RequestPart @Valid Agent agent, @RequestPart MultipartFile logo) throws IOException {
        return new ResponseEntity<Agent>(agentService.createAgent(agent, logo), HttpStatus.CREATED);
    }

    @GetMapping("/agents/{id}")
    public Agent getAgent(@PathVariable int id) {
        return agentService.getAgent(id);
    }

    @GetMapping("/agents/{id}/properties")
    public Collection<Property> getAgentProperties(@PathVariable int id) {
        return agentService.getAgentProperties(id);
    }


    @PostMapping("/agents/{id}/properties/rentals")
    public ResponseEntity<RentalProperty> addRentalPropertyToAgent(@PathVariable int id,
            @RequestPart @Valid RentalProperty property, @RequestPart MultipartFile... images) throws IOException {
        RentalProperty persistedProperty = propertyService.createRentalProperty(property, id, images);
        return new ResponseEntity<RentalProperty>(persistedProperty, HttpStatus.CREATED);
    }

    @PostMapping("/agents/{id}/properties/sales")
    public ResponseEntity<SaleProperty> addSalesPropertyToAgent(@PathVariable int id,
            @RequestPart @Valid SaleProperty property, @RequestPart MultipartFile... images) throws IOException {
        SaleProperty newProperty = propertyService.createSaleProperty(property, id, images);
        return new ResponseEntity<SaleProperty>(newProperty, HttpStatus.CREATED);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(new ApiError(fieldErrors, HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleIOException(IOException ex) {
        return new ResponseEntity<>(new ApiError(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR.toString()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handlePropertyNotFound(PropertyNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(Collections.singletonList(ex.getMessage()),
                HttpStatus.NOT_FOUND.toString()), HttpStatus.NOT_FOUND);
    }














}
