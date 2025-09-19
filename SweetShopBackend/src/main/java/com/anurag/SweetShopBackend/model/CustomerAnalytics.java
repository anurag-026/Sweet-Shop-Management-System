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
@Table(name = "customer_analytics")
public class CustomerAnalytics {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "segment_id")
    private CustomerSegment segment;
    
    @Column
    private Integer totalOrders = 0;
    
    @Column
    private Double totalSpent = 0.0;
    
    @Column
    private Double avgOrderValue = 0.0;
    
    @Column
    private LocalDateTime firstOrderDate;
    
    @Column
    private LocalDateTime lastOrderDate;
    
    @Column
    private Double orderFrequencyDays;
    
    @Column
    private Boolean isReturning = false;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
}
