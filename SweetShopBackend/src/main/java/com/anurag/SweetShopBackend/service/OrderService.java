package com.anurag.SweetShopBackend.service;

import com.anurag.SweetShopBackend.dto.CheckoutRequestDto;
import com.anurag.SweetShopBackend.dto.OrderDto;
import com.anurag.SweetShopBackend.dto.OrderItemDto;
import com.anurag.SweetShopBackend.model.*;
import com.anurag.SweetShopBackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        return checkout(user, null);
    }
    
    @Transactional
    public OrderDto checkout(User user, CheckoutRequestDto checkoutRequest) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Cannot checkout.");
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setLastUpdated(LocalDateTime.now());
        
        // Set payment information if provided
        if (checkoutRequest != null) {
            if (checkoutRequest.getPaymentMode() != null) {
                order.setPaymentMode(checkoutRequest.getPaymentMode());
            }
            if (checkoutRequest.getShippingAddress() != null) {
                order.setShippingAddress(checkoutRequest.getShippingAddress());
            }
            if (checkoutRequest.getCustomerNotes() != null) {
                order.setCustomerNotes(checkoutRequest.getCustomerNotes());
            }
            
            // Generate a payment transaction ID (in real-world, this would come from payment gateway)
            if (checkoutRequest.getPaymentDetails() != null) {
                String transactionId = generateTransactionId(checkoutRequest.getPaymentMode());
                order.setPaymentTransactionId(transactionId);
            }
            
            // Set estimated delivery date (7 days from now)
            order.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(7));
        }
        
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
        order.setLastUpdated(LocalDateTime.now());
        
        // Set actual delivery date if order is delivered
        if (status == Order.OrderStatus.DELIVERED) {
            order.setActualDeliveryDate(LocalDateTime.now());
        }
        
        Order savedOrder = orderRepository.save(order);
        
        return convertToDto(savedOrder);
    }
    
    @Transactional
    public OrderDto updateOrderTracking(UUID orderId, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        
        order.setTrackingNumber(trackingNumber);
        order.setLastUpdated(LocalDateTime.now());
        
        // If adding tracking number, set status to SHIPPED if it's still PROCESSING
        if (order.getStatus() == Order.OrderStatus.PROCESSING) {
            order.setStatus(Order.OrderStatus.SHIPPED);
        }
        
        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }
    
    private String generateTransactionId(Order.PaymentMode paymentMode) {
        String prefix = paymentMode == Order.PaymentMode.CREDIT_CARD ? "CC" : 
                       paymentMode == Order.PaymentMode.PAYPAL ? "PP" : "TX";
        return prefix + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateTrackingNumber() {
        return "TRK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setCustomerId(order.getUser().getId());
        dto.setUsername(order.getUser().getEmail());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setLastUpdated(order.getLastUpdated());
        dto.setPaymentMode(order.getPaymentMode());
        dto.setPaymentTransactionId(order.getPaymentTransactionId());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCustomerNotes(order.getCustomerNotes());
        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        dto.setActualDeliveryDate(order.getActualDeliveryDate());
        
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
