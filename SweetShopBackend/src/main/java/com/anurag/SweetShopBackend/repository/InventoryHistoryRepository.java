package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.InventoryHistory;
import com.anurag.SweetShopBackend.model.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, UUID> {
    List<InventoryHistory> findBySweetOrderByChangedAtDesc(Sweet sweet);
    
    List<InventoryHistory> findByChangeReason(String changeReason);
    
    List<InventoryHistory> findByChangedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<InventoryHistory> findBySweetAndChangedAtBetween(
        Sweet sweet, LocalDateTime startDate, LocalDateTime endDate);
}
