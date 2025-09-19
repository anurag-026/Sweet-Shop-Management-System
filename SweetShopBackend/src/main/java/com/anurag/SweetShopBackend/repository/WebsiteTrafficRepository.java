package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.WebsiteTraffic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface WebsiteTrafficRepository extends JpaRepository<WebsiteTraffic, UUID> {
    List<WebsiteTraffic> findByVisitDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT COUNT(wt) FROM WebsiteTraffic wt WHERE wt.visitDate BETWEEN ?1 AND ?2")
    Long countTotalVisits(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT COUNT(DISTINCT wt.sessionId) FROM WebsiteTraffic wt WHERE wt.visitDate BETWEEN ?1 AND ?2")
    Long countUniqueVisitors(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT CASE WHEN COUNT(DISTINCT wt.sessionId) = 0 THEN 0.0 " +
           "ELSE (SUM(CASE WHEN wt.isBounce = true THEN 1 ELSE 0 END) * 100.0) / COUNT(DISTINCT wt.sessionId) END " +
           "FROM WebsiteTraffic wt WHERE wt.visitDate BETWEEN ?1 AND ?2")
    Double getBounceRate(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT AVG(wt.sessionDurationSeconds) FROM WebsiteTraffic wt WHERE wt.visitDate BETWEEN ?1 AND ?2")
    Double getAvgSessionDuration(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(wt.pageViews) FROM WebsiteTraffic wt WHERE wt.visitDate BETWEEN ?1 AND ?2")
    Long getTotalPageViews(LocalDate startDate, LocalDate endDate);
}
