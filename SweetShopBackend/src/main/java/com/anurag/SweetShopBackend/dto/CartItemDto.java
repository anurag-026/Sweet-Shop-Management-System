package com.anurag.SweetShopBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private UUID id;
    private UUID sweetId;
    private String sweetName;
    private String category;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
    private String description;
    private String image;
}
