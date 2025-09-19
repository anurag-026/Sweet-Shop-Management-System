package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SweetRepository extends JpaRepository<Sweet, UUID> {
    List<Sweet> findByNameContainingIgnoreCase(String name);
    List<Sweet> findByCategoryContainingIgnoreCase(String category);
    List<Sweet> findByPriceBetween(Double minPrice, Double maxPrice);
    
    // Analytics-related methods
    long countByQuantityGreaterThan(Integer threshold);
    long countByQuantityBetween(Integer min, Integer max);
    long countByQuantity(Integer quantity);
    
    List<Sweet> findByQuantityLessThan(Integer threshold);
    
    @Query("SELECT SUM(s.price * s.quantity) FROM Sweet s")
    Double calculateTotalInventoryValue();
}
