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
@Table(name = "conversion_events")
public class ConversionEvent {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @NotBlank
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @NotBlank
    @Column(name = "event_type", nullable = false)
    private String eventType; // 'visit', 'product_view', 'add_to_cart', 'checkout', 'purchase'
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Sweet product;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime eventDate;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}
