package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrder(com.anurag.SweetShopBackend.model.Order order);
}
