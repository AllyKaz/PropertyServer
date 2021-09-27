package com.example.PropertyDemo;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.PropertyBaseRepository;
import com.example.PropertyDemo.Repositories.RentalPropertyRepository;
import com.example.PropertyDemo.Repositories.SalePropertyRepository;
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
    S3Service s3Service;

    public RentalProperty createRentalProperty(RentalProperty property, int agentId, MultipartFile[] images) throws IOException {
        Agent agent = agentRepository.findById(agentId).orElseThrow();
        for (MultipartFile multipartFile : images) {
            property.addImage(s3Service.save(multipartFile));
        }
        property.setAgent(agent);
        return rentalPropertyRepository.save(property);
    }

    public SaleProperty createSaleProperty(SaleProperty property, int agentId, MultipartFile[] images) throws IOException {
        Agent agent = agentRepository.findById(agentId).orElseThrow();
        for(MultipartFile multipartFile : images) {
            property.addImage(s3Service.save(multipartFile));
        }
        property.setAgent(agent);
        return salePropertyRepository.save(property);
    }

    public RentalProperty getRentalProperty(int id) {
        return rentalPropertyRepository.findById(id).orElseThrow();
    }

    public Property getProperty(Integer id) {
        return propertyBaseRepository.findById(id).orElseThrow();
    }

    public List<Property> getAllProperties(Map<String, String> searchParams) {
        return propertyBaseRepository.findAll(SpecificationBuilder.buildSpecification(searchParams));
    }

    public List<RentalProperty> getAllRentalProperties(Map<String, String> searchParams) {
        return rentalPropertyRepository.findAll(SpecificationBuilder.buildSpecification(searchParams));
    }

    public List<SaleProperty> getAllSaleProperties(Map<String, String> searchParams) {
        return salePropertyRepository.findAll(SpecificationBuilder.buildSpecification(searchParams));
    }

    //Todo Implement get all properties for sales and rentals
}
