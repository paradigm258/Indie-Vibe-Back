package com.swp493.ivb.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWSConfig
 */
@Configuration
public class AWSConfig {

    public static final String BUCKET_NAME = "indievibe-storage";
    public static final String BUCKET_URL = "https://"+BUCKET_NAME+".s3.amazonaws.com/";

    @Bean
    public AmazonS3 S3Instance() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        return s3;
    }
}