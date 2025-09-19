package com.anurag.SweetShopBackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_analytics")
public class ProductAnalytics {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "sweet_id", nullable = false)
    private Sweet sweet;
    
    @Column
    private Integer unitsSold = 0;
    
    @Column
    private Double revenue = 0.0;
    
    @Column
    private Double profit = 0.0;
    
    @Column
    private Integer viewCount = 0;
    
    @Column
    private Integer addToCartCount = 0;
    
    @Column
    private Double conversionRate = 0.0;
    
    @NotNull
    @Column(nullable = false)
    private String timePeriod; // 'daily', 'weekly', 'monthly', 'yearly'
    
    @NotNull
    @Column(nullable = false)
    private LocalDate periodStart;
    
    @NotNull
    @Column(nullable = false)
    private LocalDate periodEnd;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
}
