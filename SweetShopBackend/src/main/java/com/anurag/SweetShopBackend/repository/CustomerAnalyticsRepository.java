package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.CustomerAnalytics;
import com.anurag.SweetShopBackend.model.CustomerSegment;
import com.anurag.SweetShopBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerAnalyticsRepository extends JpaRepository<CustomerAnalytics, UUID> {
    Optional<CustomerAnalytics> findByUser(User user);
    
    List<CustomerAnalytics> findBySegment(CustomerSegment segment);
    
    @Query("SELECT COUNT(ca) FROM CustomerAnalytics ca")
    Long countTotalCustomers();
    
    @Query("SELECT COUNT(ca) FROM CustomerAnalytics ca WHERE ca.isReturning = true")
    Long countReturningCustomers();
    
    @Query("SELECT COUNT(ca) FROM CustomerAnalytics ca WHERE ca.firstOrderDate >= ?1")
    Long countNewCustomers(java.time.LocalDateTime since);
    
    @Query("SELECT AVG(ca.avgOrderValue) FROM CustomerAnalytics ca")
    Double getAverageCustomerValue();

    // Retention rate = returning / total * 100
    @Query("SELECT (SUM(CASE WHEN ca.isReturning = true THEN 1 ELSE 0 END) * 1.0 / NULLIF(COUNT(ca),0)) * 100.0 FROM CustomerAnalytics ca")
    Double getCustomerRetentionRate();
    
    
    @Query("SELECT ca.segment.segmentName, COUNT(ca) as count, AVG(ca.avgOrderValue) as avgValue " +
           "FROM CustomerAnalytics ca GROUP BY ca.segment.segmentName ORDER BY count DESC")
    List<Object[]> getTopCustomerSegments();
}
