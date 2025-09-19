package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.model.Sweet;
import com.anurag.SweetShopBackend.service.SweetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sweets")
public class SweetController {

    @Autowired
    private SweetService sweetService;

    @GetMapping
    public ResponseEntity<List<Sweet>> getAllSweets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max) {
        List<Sweet> sweets = sweetService.getAllSweets(name, category, min, max);
        return ResponseEntity.ok(sweets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sweet> getSweetById(@PathVariable UUID id) {
        Sweet sweet = sweetService.getSweetById(id);
        return ResponseEntity.ok(sweet);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sweet> createSweet(@Valid @RequestBody Sweet sweet) {
        Sweet createdSweet = sweetService.createSweet(sweet);
        return ResponseEntity.ok(createdSweet);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sweet> updateSweet(@PathVariable UUID id, @Valid @RequestBody Sweet sweet) {
        Sweet updatedSweet = sweetService.updateSweet(id, sweet);
        return ResponseEntity.ok(updatedSweet);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSweet(@PathVariable UUID id) {
        sweetService.deleteSweet(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<Sweet> purchaseSweet(
            @PathVariable UUID id,
            @RequestParam(required = true) @Min(value = 1, message = "Quantity must be at least 1") Integer qty) {
        
        Sweet sweet = sweetService.purchaseSweet(id, qty);
        return ResponseEntity.ok(sweet);
    }

    @PostMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sweet> restockSweet(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "1") Integer qty) {
        
        Sweet sweet = sweetService.restockSweet(id, qty);
        return ResponseEntity.ok(sweet);
    }
}
