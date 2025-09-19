package com.anurag.SweetShopBackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suppliers")
public class Supplier {
    
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
    private String name;
    
    @Column
    private String contactPerson;
    
    @Column
    private String email;
    
    @Column
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column
    private String website;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column
    private Boolean isActive = true;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "supplier")
    private List<Sweet> sweets;
}
