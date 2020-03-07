package com.swp493.ivb.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.context.annotation.Bean;

/**
 * AWSConfig
 */
public class AWSConfig {

    @Bean
    public AmazonS3 S3Instance() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        return s3;
    }
}