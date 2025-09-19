package com.anurag.SweetShopBackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "inventory_history")
public class InventoryHistory {
    
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
    
    @NotNull
    @Column(nullable = false)
    private Integer previousQuantity;
    
    @NotNull
    @Column(nullable = false)
    private Integer newQuantity;
    
    @NotBlank
    @Column(name = "change_reason", nullable = false)
    private String changeReason; // 'restock', 'sale', 'adjustment', 'return', 'damaged'
    
    @Column
    private UUID referenceId; // Could be order_id, supplier_order_id, etc.
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne
    @JoinColumn(name = "changed_by")
    private User changedBy;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();
}
