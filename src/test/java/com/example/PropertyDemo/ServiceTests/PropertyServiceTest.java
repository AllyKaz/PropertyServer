package com.example.PropertyDemo.ServiceTests;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.PropertyBaseRepository;
import com.example.PropertyDemo.Repositories.RentalPropertyRepository;
import com.example.PropertyDemo.Repositories.SalePropertyRepository;
import com.example.PropertyDemo.Services.PropertyService;
import com.example.PropertyDemo.Services.S3Service;
import com.example.PropertyDemo.SpecificationBuilders.SpecificationBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.PropertyDemo.Builders.BuilderDirector.*;
import static com.example.PropertyDemo.TestUtils.PropertyDemoTestHelper.generateId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
public class PropertyServiceTest {

    @MockBean
    RentalPropertyRepository rentalPropertyRepository;

    @MockBean
    AgentRepository agentRepository;

    @MockBean
    PropertyBaseRepository<Property> propertyBaseRepository;

    @MockBean
    SalePropertyRepository salePropertyRepository;

    @MockBean
    S3Service s3Service;

    @MockBean
    SpecificationBuilder specificationBuilder;

    @Configuration
    static class Config {

        @Bean
        PropertyService propertyService() {
            return new PropertyService();
        }
    }

    @Autowired
    PropertyService propertyService;

    @Test
    public void createRentalProperty() throws IOException {
        RentalProperty property = mock(RentalProperty.class);
        MultipartFile file1 = mock(MockMultipartFile.class), file2 = mock(MockMultipartFile.class),
                file3 = mock(MockMultipartFile.class);
        Agent agent = mock(Agent.class);
        URL url = new URL("https://url");
        int AGENT_ID = 1;

        when(agentRepository.findById(AGENT_ID)).thenReturn(Optional.ofNullable(agent));
        when(rentalPropertyRepository.save(property)).thenReturn(property);
        when(s3Service.save(any(MockMultipartFile.class), anyString())).thenReturn(url);

        RentalProperty rentalProperty =
                propertyService.createRentalProperty(property, AGENT_ID, new MultipartFile[]{file1, file2, file3});

        assertThat(rentalProperty).isEqualTo(property);
        verify(property, times(3)).addImage(url);
    }

    @Test
    public void createSaleProperty() throws IOException {
        SaleProperty property = mock(SaleProperty.class);
        MultipartFile file1 = mock(MockMultipartFile.class), file2 = mock(MockMultipartFile.class),
                file3 = mock(MockMultipartFile.class);
        Agent agent = mock(Agent.class);
        URL url = new URL("https://url");
        int AGENT_ID = generateId();

        when(agentRepository.findById(AGENT_ID)).thenReturn(Optional.of(agent));
        when(salePropertyRepository.save(property)).thenReturn(property);
        when(s3Service.save(any(MockMultipartFile.class), anyString())).thenReturn(url);

        SaleProperty returnedProperty = propertyService.createSaleProperty(property, AGENT_ID,
                new MultipartFile[]{file1, file2, file3});

        assertThat(returnedProperty).isEqualTo(property);
        verify(property, times(3)).addImage(url);
    }

    @Test
    public void getPropertyById() throws MalformedURLException {
        Property property = mock(Property.class);
        int ID = generateId();
        when(property.getId()).thenReturn(ID);

        when(propertyBaseRepository.findById(ID)).thenReturn(Optional.of(property));

        Property returnedProperty = propertyService.getProperty(ID);

        assertThat(returnedProperty).isEqualTo(property);

    }

    @Test
    public void getPropertiesBySpecification() {
        Agent agent = initAgent().build();
        List<Property> properties = Arrays.asList(initRentalProperty(agent).build(),
                initSaleProperty(agent).build(), initRentalProperty(agent).build(), initSaleProperty(agent).build());
        Specification<Property> spec = mock(Specification.class);
        Map<String, String> params = Map.of("KEY_1", "VALUE_1", "KEY_2", "VALUE_2");
        when(specificationBuilder.build(params)).thenReturn(spec);
        when(propertyBaseRepository.findAll(spec)).thenReturn(properties);

        List<Property> returnedProperties = propertyService.getAllProperties(params);

        assertThat(returnedProperties).isEqualTo(properties);
    }

    @Test
    public void getRentalPropertiesBySpecification() {
        Agent agent = initAgent().build();
        List<RentalProperty> properties = Arrays.asList(initRentalProperty(agent).build(),
                initRentalProperty(agent).build(), initRentalProperty(agent).build());
        Specification<RentalProperty> spec = mock(Specification.class);
        Map<String, String> params = Map.of("KEY_1", "VALUE_1", "KEY_2", "VALUE_2");
        when(specificationBuilder.<RentalProperty>build(params)).thenReturn(spec);
        when(rentalPropertyRepository.findAll(spec)).thenReturn(properties);

        List<RentalProperty> returnedProperties = propertyService.getAllRentalProperties(params);

        assertThat(returnedProperties).isEqualTo(properties);
    }

    @Test
    public void getSalePropertiesBySpecification() {
        Agent agent = initAgent().build();
        List<SaleProperty> properties = Arrays.asList(initSaleProperty(agent).build(),
                initSaleProperty(agent).build(), initSaleProperty(agent).build());
        Specification<SaleProperty> spec = mock(Specification.class);
        Map<String, String> params = Map.of("KEY_1", "VALUE_1", "KEY_2", "VALUE_2");
        when(specificationBuilder.<SaleProperty>build(params)).thenReturn(spec);
        when(salePropertyRepository.findAll(spec)).thenReturn(properties);

        List<SaleProperty> returnedProperties = propertyService.getAllSaleProperties(params);

        assertThat(returnedProperties).isEqualTo(properties);
    }







}

