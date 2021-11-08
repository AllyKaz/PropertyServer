package com.example.PropertyServer.ServiceTests;

import com.example.PropertyServer.Agent.Agent;
import com.example.PropertyServer.Exceptions.AgentNotFoundException;
import com.example.PropertyServer.Property.Property;
import com.example.PropertyServer.Property.RentalProperty;
import com.example.PropertyServer.Property.SaleProperty;
import com.example.PropertyServer.Exceptions.PropertyNotFoundException;
import com.example.PropertyServer.Repositories.AgentRepository;
import com.example.PropertyServer.Repositories.PropertyBaseRepository;
import com.example.PropertyServer.Repositories.RentalPropertyRepository;
import com.example.PropertyServer.Repositories.SalePropertyRepository;
import com.example.PropertyServer.Services.PropertyService;
import com.example.PropertyServer.Services.S3Service;
import com.example.PropertyServer.SpecificationBuilders.SpecificationBuilder;
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

import static com.example.PropertyServer.Builders.BuilderDirector.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        int AGENT_ID = 1;

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
        int ID = 1;
        when(property.getId()).thenReturn(ID);

        when(propertyBaseRepository.findById(ID)).thenReturn(Optional.of(property));

        Property returnedProperty = propertyService.getProperty(ID);

        assertThat(returnedProperty).isEqualTo(property);

    }

    @Test
    public void nonExistentPropertyThrowsException() {
        final int ID = 1;
        when(propertyBaseRepository.findById(ID)).thenThrow(new PropertyNotFoundException(ID));

        PropertyNotFoundException ex = assertThrows(PropertyNotFoundException.class, () -> propertyService.getProperty(ID));

        assertThat("Property with id = " + ID + " not found.").isEqualTo(ex.getMessage());
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

    @Test
    public void createSalePropertyThrowsAgentNotFoundException() {
        int AGENT_ID = 1;
        when(agentRepository.findById(AGENT_ID)).thenThrow(new AgentNotFoundException(AGENT_ID));
        assertThrows(AgentNotFoundException.class, () -> propertyService.createSaleProperty(
                mock(SaleProperty.class), AGENT_ID, new MultipartFile[]{mock(MultipartFile.class)}));
    }

    @Test
    public void createRentalPropertyThrowsAgentNotFoundException() {
        int AGENT_ID = 1;
        when(agentRepository.findById(AGENT_ID)).thenThrow(new AgentNotFoundException(AGENT_ID));
        assertThrows(AgentNotFoundException.class, () -> propertyService.createRentalProperty(
                mock(RentalProperty.class), AGENT_ID, new MultipartFile[]{mock(MultipartFile.class)}));
    }

    @Test
    public void addImagesThrowsPropertyNotFound() {
        int PROPERTY_ID = 1;
        when(propertyBaseRepository.findById(PROPERTY_ID)).thenThrow(new PropertyNotFoundException(PROPERTY_ID));

        assertThrows(PropertyNotFoundException.class, () -> propertyService.addImagesToProperty(PROPERTY_ID,
                new MultipartFile[]{mock(MultipartFile.class)}));
    }







}

