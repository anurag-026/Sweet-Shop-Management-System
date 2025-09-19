package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.Order;
import com.anurag.SweetShopBackend.model.ShippingAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShippingAnalyticsRepository extends JpaRepository<ShippingAnalytics, UUID> {
    Optional<ShippingAnalytics> findByOrder(Order order);
    
    @Query("SELECT AVG(sa.shippingTimeDays) FROM ShippingAnalytics sa " +
           "WHERE sa.order.orderDate BETWEEN ?1 AND ?2")
    Double getAverageShippingTime(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT CASE WHEN COUNT(sa) = 0 THEN 0.0 " +
           "ELSE (SUM(CASE WHEN sa.isOnTime = true THEN 1 ELSE 0 END) * 100.0) / COUNT(sa) END " +
           "FROM ShippingAnalytics sa WHERE sa.order.orderDate BETWEEN ?1 AND ?2")
    Double getOnTimeDeliveryRate(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT AVG(sa.shippingCost) FROM ShippingAnalytics sa " +
           "WHERE sa.order.orderDate BETWEEN ?1 AND ?2")
    Double getAverageShippingCost(LocalDateTime startDate, LocalDateTime endDate);
}
