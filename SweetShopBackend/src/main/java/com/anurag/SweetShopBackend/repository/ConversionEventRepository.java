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
}
