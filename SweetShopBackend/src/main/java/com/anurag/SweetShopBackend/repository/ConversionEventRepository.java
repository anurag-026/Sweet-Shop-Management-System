package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.ConversionEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConversionEventRepository extends JpaRepository<ConversionEvent, UUID> {
    List<ConversionEvent> findBySessionId(String sessionId);
    
    List<ConversionEvent> findByEventDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(ce) FROM ConversionEvent ce WHERE ce.eventType = ?1 AND ce.eventDate BETWEEN ?2 AND ?3")
    Long countEventsByType(String eventType, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT ce.eventType, COUNT(ce) FROM ConversionEvent ce " +
           "WHERE ce.eventDate BETWEEN ?1 AND ?2 GROUP BY ce.eventType")
    List<Object[]> getConversionFunnel(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(DISTINCT ce.sessionId) FROM ConversionEvent ce WHERE ce.eventType = ?1 AND ce.eventDate BETWEEN ?2 AND ?3")
    Long countUniqueSessionsByEventType(String eventType, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT ce.product, COUNT(ce) FROM ConversionEvent ce WHERE ce.eventType = 'product_view' AND ce.eventDate BETWEEN ?1 AND ?2 GROUP BY ce.product ORDER BY COUNT(ce) DESC")
    List<Object[]> getTopViewedProducts(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT ce.product, COUNT(ce) FROM ConversionEvent ce WHERE ce.eventType = 'add_to_cart' AND ce.eventDate BETWEEN ?1 AND ?2 GROUP BY ce.product ORDER BY COUNT(ce) DESC")
    List<Object[]> getTopAddedToCartProducts(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(ce) FROM ConversionEvent ce WHERE ce.eventType = ?1 AND ce.eventDate BETWEEN ?2 AND ?3 AND ce.user IS NOT NULL")
    Long countEventsByTypeAndUser(String eventType, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT ce.eventType, COUNT(DISTINCT ce.user) FROM ConversionEvent ce WHERE ce.eventDate BETWEEN ?1 AND ?2 AND ce.user IS NOT NULL GROUP BY ce.eventType")
    List<Object[]> getConversionFunnelByUsers(LocalDateTime startDate, LocalDateTime endDate);
}
