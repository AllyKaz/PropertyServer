package com.example.PropertyServer.Config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.PropertyServer.Property.Property;
import com.example.PropertyServer.SpecificationBuilders.RentalPropertySpecificationBuilder;
import com.example.PropertyServer.SpecificationBuilders.SalePropertySpecificationBuilder;
import com.example.PropertyServer.SpecificationBuilders.SpecificationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

    @Bean
    public AmazonS3 getAmazonS3Client() {
        return AmazonS3ClientBuilder.defaultClient();
    }

    @Bean
    public SpecificationBuilder<Property> getSpecificationBuilder() {
        return new SpecificationBuilder<Property>();
    }

    @Bean
    public RentalPropertySpecificationBuilder getRentalPropertySpecificationBuilder() {
        return new RentalPropertySpecificationBuilder();
    }

    @Bean
    public SalePropertySpecificationBuilder getSalePropertySpecificationBuilder() {
        return new SalePropertySpecificationBuilder();
    }
}
