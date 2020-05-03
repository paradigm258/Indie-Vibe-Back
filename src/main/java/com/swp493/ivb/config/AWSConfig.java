package com.swp493.ivb.config;

import java.util.Date;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWSConfig
 */
@Configuration
public class AWSConfig {

    @Value(value = "${BUCKET_NAME:indievibe-storage}")
    public static String BUCKET_NAME;
    public static String BUCKET_URL;
    
    public AWSConfig(@Value(value = "${BUCKET_NAME:indievibe-storage}") String bucketName){
        BUCKET_NAME = bucketName;
        BUCKET_URL = "https://"+BUCKET_NAME+".s3.amazonaws.com/";
    }
    public static Date presignExpiration(){
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10 ;
        expiration.setTime(expTimeMillis);
        return expiration;
    }
    @Bean
    public AmazonS3 S3Instance() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_1).build();
        return s3;
    }
}