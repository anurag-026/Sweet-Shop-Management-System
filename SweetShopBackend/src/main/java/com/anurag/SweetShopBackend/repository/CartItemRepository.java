package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.CartItem;
import com.anurag.SweetShopBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndSweet(User user, com.anurag.SweetShopBackend.model.Sweet sweet);
    void deleteByUser(User user);
}
