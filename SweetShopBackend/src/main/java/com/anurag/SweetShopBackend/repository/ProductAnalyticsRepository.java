package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.ProductAnalytics;
import com.anurag.SweetShopBackend.model.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductAnalyticsRepository extends JpaRepository<ProductAnalytics, UUID> {
    List<ProductAnalytics> findBySweetOrderByPeriodStartDesc(Sweet sweet);
    
    Optional<ProductAnalytics> findBySweetAndTimePeriodAndPeriodStart(
        Sweet sweet, String timePeriod, LocalDate periodStart);
    
    List<ProductAnalytics> findByTimePeriodAndPeriodStartBetweenOrderByUnitsSoldDesc(
        String timePeriod, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT pa FROM ProductAnalytics pa WHERE pa.timePeriod = ?1 AND pa.periodStart = ?2 ORDER BY pa.revenue DESC")
    List<ProductAnalytics> findTopProductsByRevenue(String timePeriod, LocalDate periodStart);
    
    @Query("SELECT pa FROM ProductAnalytics pa WHERE pa.timePeriod = ?1 AND pa.periodStart = ?2 ORDER BY pa.unitsSold DESC")
    List<ProductAnalytics> findTopProductsByUnitsSold(String timePeriod, LocalDate periodStart);
}
