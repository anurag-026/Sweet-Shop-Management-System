package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.SystemAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SystemAlertRepository extends JpaRepository<SystemAlert, UUID> {
    List<SystemAlert> findByAlertType(String alertType);
    
    List<SystemAlert> findByPriority(String priority);
    
    List<SystemAlert> findByIsReadFalse();
    
    List<SystemAlert> findByIsDismissedFalse();
    
    List<SystemAlert> findByIsDismissedFalseOrderByCreatedAtDesc();
    
    List<SystemAlert> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startDate, LocalDateTime endDate);
}
