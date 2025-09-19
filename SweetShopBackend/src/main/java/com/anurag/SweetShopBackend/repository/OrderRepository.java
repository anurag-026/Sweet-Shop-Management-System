package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.Order;
import com.anurag.SweetShopBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    // Analytics-related methods
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2")
    Long countOrdersBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2")
    Double sumTotalAmountBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT AVG(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2")
    Double avgTotalAmountBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2 GROUP BY o.status")
    List<Object[]> countOrdersByStatus(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT FUNCTION('YEAR', o.orderDate) as year, FUNCTION('MONTH', o.orderDate) as month, " +
           "COUNT(o) as count, SUM(o.totalAmount) as total " +
           "FROM Order o GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlySalesData();
}
