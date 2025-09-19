package com.anurag.SweetShopBackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
    @NotNull
    @Column(nullable = false)
    private Double totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Column(nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();
    
    @Column
    private LocalDateTime lastUpdated = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column
    private PaymentMode paymentMode = PaymentMode.CREDIT_CARD;
    
    @Column
    private String paymentTransactionId;
    
    @Column
    private String shippingAddress;
    
    @Column
    private String customerNotes;
    
    @Column
    private String trackingNumber;
    
    @Column
    private LocalDateTime estimatedDeliveryDate;
    
    @Column
    private LocalDateTime actualDeliveryDate;
    
    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, REFUNDED
    }
    
    public enum PaymentMode {
        CREDIT_CARD, PAYPAL, BANK_TRANSFER, CASH_ON_DELIVERY
    }
}
