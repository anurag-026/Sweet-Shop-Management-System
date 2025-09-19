package com.anurag.SweetShopBackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "system_alerts")
public class SystemAlert {
    
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
    private String alertType; // 'warning', 'info', 'success', 'error'
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @NotBlank
    @Column(nullable = false)
    private String priority; // 'low', 'medium', 'high'
    
    @Column
    private Boolean isRead = false;
    
    @Column
    private Boolean isDismissed = false;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
}
