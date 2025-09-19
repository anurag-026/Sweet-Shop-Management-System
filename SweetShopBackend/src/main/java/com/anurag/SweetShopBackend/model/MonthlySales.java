package com.anurag.SweetShopBackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "monthly_sales")
public class MonthlySales {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @NotNull
    @Column(nullable = false)
    private Integer month;
    
    @NotNull
    @Column(nullable = false)
    private Integer year;
    
    @Column
    private Double totalRevenue = 0.0;
    
    @Column
    private Integer totalOrders = 0;
    
    @Column
    private Double totalProfit = 0.0;
    
    @Column
    private Double avgOrderValue = 0.0;
    
    @Column
    private Double growthPercentage;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
}
