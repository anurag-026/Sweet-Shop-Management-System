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
@Table(name = "order_analytics")
public class OrderAnalytics {
    
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
    private Integer timeToProcessMinutes;
    
    @Column
    private Integer itemsCount;
    
    @Column
    private Double discountAmount = 0.0;
    
    @Column
    private Double taxAmount = 0.0;
    
    @Column
    private Double profitAmount = 0.0;
    
    @Column
    private Double profitMargin = 0.0;
    
    @Column
    private String acquisitionChannel; // 'direct', 'search', 'social', 'email', 'referral'
    
    @Column
    private String deviceType; // 'desktop', 'mobile', 'tablet'
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
}
