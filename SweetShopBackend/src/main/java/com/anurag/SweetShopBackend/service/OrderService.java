package com.anurag.SweetShopBackend.service;

import com.anurag.SweetShopBackend.dto.OrderDto;
import com.anurag.SweetShopBackend.dto.OrderItemDto;
import com.anurag.SweetShopBackend.model.*;
import com.anurag.SweetShopBackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private SweetRepository sweetRepository;

    @Transactional
    public OrderDto checkout(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Cannot checkout.");
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(java.time.LocalDateTime.now());
        
        double totalAmount = 0.0;
        
        // Create order items and calculate total
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setSweet(cartItem.getSweet());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setTotalPrice(cartItem.getPrice() * cartItem.getQuantity());
            
            totalAmount += orderItem.getTotalPrice();
            
            // Update sweet quantity
            Sweet sweet = cartItem.getSweet();
            if (sweet.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock available for sweet: " + sweet.getName());
            }
            sweet.setQuantity(sweet.getQuantity() - cartItem.getQuantity());
            sweetRepository.save(sweet);
        }
        
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        
        // Save order items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setSweet(cartItem.getSweet());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setTotalPrice(cartItem.getPrice() * cartItem.getQuantity());
            orderItemRepository.save(orderItem);
        }
        
        // Clear cart
        cartItemRepository.deleteByUser(user);
        
        return convertToDto(savedOrder);
    }

    public List<OrderDto> getUserOrders(User user) {
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(UUID orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only view your own orders");
        }
        
        return convertToDto(order);
    }

    @Transactional
    public OrderDto updateOrderStatus(UUID orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        
        return convertToDto(savedOrder);
    }

    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUsername(order.getUser().getEmail());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        List<OrderItemDto> orderItemDtos = orderItems.stream()
                .map(this::convertOrderItemToDto)
                .collect(Collectors.toList());
        dto.setOrderItems(orderItemDtos);
        
        return dto;
    }

    private OrderItemDto convertOrderItemToDto(OrderItem orderItem) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(orderItem.getId());
        dto.setSweetId(orderItem.getSweet().getId());
        dto.setSweetName(orderItem.getSweet().getName());
        dto.setCategory(orderItem.getSweet().getCategory());
        dto.setPrice(orderItem.getPrice());
        dto.setQuantity(orderItem.getQuantity());
        dto.setTotalPrice(orderItem.getTotalPrice());
        dto.setDescription(orderItem.getSweet().getDescription());
        dto.setImage(orderItem.getSweet().getImage());
        return dto;
    }
}
