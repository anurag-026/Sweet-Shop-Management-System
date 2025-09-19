package com.anurag.SweetShopBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shipping_analytics")
public class ShippingAnalytics {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column
    private String shippingCarrier;
    
    @Column
    private Double shippingCost;
    
    @Column
    private LocalDateTime estimatedDeliveryDate;
    
    @Column
    private LocalDateTime actualDeliveryDate;
    
    @Column
    private Double shippingTimeDays;
    
    @Column
    private Boolean isOnTime;
    
    @Column
    private Integer trackingUpdates = 0;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
}
