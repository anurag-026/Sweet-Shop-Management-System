package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.Order;
import com.anurag.SweetShopBackend.model.OrderAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderAnalyticsRepository extends JpaRepository<OrderAnalytics, UUID> {
    Optional<OrderAnalytics> findByOrder(Order order);
    
    @Query("SELECT SUM(oa.profitAmount) FROM OrderAnalytics oa " +
           "WHERE oa.order.orderDate BETWEEN ?1 AND ?2")
    Double getTotalProfit(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT AVG(oa.profitMargin) FROM OrderAnalytics oa " +
           "WHERE oa.order.orderDate BETWEEN ?1 AND ?2")
    Double getAverageProfitMargin(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT oa.acquisitionChannel, COUNT(oa) FROM OrderAnalytics oa " +
           "WHERE oa.order.orderDate BETWEEN ?1 AND ?2 GROUP BY oa.acquisitionChannel")
    Object[] getOrdersByAcquisitionChannel(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT oa.deviceType, COUNT(oa) FROM OrderAnalytics oa " +
           "WHERE oa.order.orderDate BETWEEN ?1 AND ?2 GROUP BY oa.deviceType")
    Object[] getOrdersByDeviceType(LocalDateTime startDate, LocalDateTime endDate);
}
