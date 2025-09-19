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
@Table(name = "website_traffic")
public class WebsiteTraffic {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @NotBlank
    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column
    private String ipAddress;
    
    @Column(columnDefinition = "TEXT")
    private String userAgent;
    
    @Column
    private String referrer;
    
    @Column
    private String landingPage;
    
    @Column
    private String exitPage;
    
    @Column
    private Integer pageViews = 1;
    
    @Column
    private Integer sessionDurationSeconds;
    
    @Column
    private Boolean isBounce = false;
    
    @NotNull
    @Column(nullable = false)
    private LocalDate visitDate;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}
