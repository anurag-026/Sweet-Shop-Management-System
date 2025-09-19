package com.anurag.SweetShopBackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {
    
    @NotNull
    private UUID sweetId;
    
    @NotNull
    @Min(1)
    private Integer quantity;
}
