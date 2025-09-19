package com.anurag.SweetShopBackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "category_sales")
public class CategorySales {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @NotBlank
    @Column(nullable = false)
    private String category;
    
    @Column
    private Integer unitsSold = 0;
    
    @Column
    private Double revenue = 0.0;
    
    @Column
    private Double profit = 0.0;
    
    @Column
    private Double percentage = 0.0;
    
    @NotBlank
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
