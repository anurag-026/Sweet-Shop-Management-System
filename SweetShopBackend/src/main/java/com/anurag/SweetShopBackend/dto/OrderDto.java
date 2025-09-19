package com.anurag.SweetShopBackend.dto;

import com.anurag.SweetShopBackend.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private String username;
    private List<OrderItemDto> orderItems;
    private Double totalAmount;
    private Order.OrderStatus status;
    private LocalDateTime orderDate;
}
