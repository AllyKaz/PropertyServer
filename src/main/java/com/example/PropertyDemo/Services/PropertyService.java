package com.example.PropertyDemo.Services;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.PropertyBaseRepository;
import com.example.PropertyDemo.Repositories.RentalPropertyRepository;
import com.example.PropertyDemo.Repositories.SalePropertyRepository;
import com.example.PropertyDemo.SpecificationBuilders.SpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class PropertyService {

    @Autowired
    RentalPropertyRepository rentalPropertyRepository;

    @Autowired
    AgentRepository agentRepository;

    @Autowired
    PropertyBaseRepository<Property> propertyBaseRepository;

    @Autowired
    SalePropertyRepository salePropertyRepository;

    @Autowired
    SpecificationBuilder<Property> specificationBuilder;

    @Autowired
    SpecificationBuilder<RentalProperty> rentalPropertySpecificationBuilder;

    @Autowired
    SpecificationBuilder<SaleProperty> salePropertySpecificationBuilder;

    @Autowired
    S3Service s3Service;

    public RentalProperty createRentalProperty(RentalProperty property, int agentId, MultipartFile[] images) throws IOException {
        Agent agent = agentRepository.findById(agentId).orElseThrow();
        property = rentalPropertyRepository.save(property);
        addImages(property, images);
        property.setAgent(agent);
        return rentalPropertyRepository.save(property);
    }

    public SaleProperty createSaleProperty(SaleProperty property, int agentId, MultipartFile[] images) throws IOException {
        Agent agent = agentRepository.findById(agentId).orElseThrow();
        property = salePropertyRepository.save(property);
        addImages(property, images);
        property.setAgent(agent);
        return salePropertyRepository.save(property);
    }

    private void addImages(Property property, MultipartFile[] images) throws IOException {
        for(MultipartFile multipartFile : images) {
            property.addImage(s3Service.save(multipartFile,
                    buildFilename(property.getId(), property.getImages().size())));
        }
    }

    public Property addImagesToProperty(int imageId, MultipartFile[] images) throws IOException {
        Property property = propertyBaseRepository.findById(imageId).orElseThrow();
        addImages(property, images);
        return propertyBaseRepository.save(property);
    }

    public RentalProperty getRentalProperty(int id) {
        return rentalPropertyRepository.findById(id).orElseThrow();
    }

    public Property getProperty(Integer id) {
        return propertyBaseRepository.findById(id).orElseThrow();
    }

    public List<Property> getAllProperties(Map<String, String> searchParams) {
        return propertyBaseRepository.findAll(specificationBuilder.build(searchParams));
    }

    public List<RentalProperty> getAllRentalProperties(Map<String, String> searchParams) {
        return rentalPropertyRepository.findAll(rentalPropertySpecificationBuilder.build(searchParams));
    }

    public List<SaleProperty> getAllSaleProperties(Map<String, String> searchParams) {
        return salePropertyRepository.findAll(salePropertySpecificationBuilder.build(searchParams));
    }

    private String buildFilename(int id, int index) {
        return "property_" + id + "_image_" + index;
    }

}
