package com.swp493.ivb.features.cms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryRevenueRecord extends JpaRepository<EntityRevenueRecord,String>{
    @Query(value = "select ifnull(sum(amount),0) from revenue_record where year(recorded_month) = :year and premium_type = :type",nativeQuery = true)
    long getYearRevenue(int year, String type);
    @Query(value = "select ifnull(sum(amount),0) from revenue_record where year(recorded_month) = :year and month(recorded_month) = :month and premium_type = :type", nativeQuery = true)
    long getMonthRevenue(int year, int month, String type);
}