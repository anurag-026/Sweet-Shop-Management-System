package com.anurag.SweetShopBackend.service;

import com.anurag.SweetShopBackend.model.Sweet;
import com.anurag.SweetShopBackend.repository.SweetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SweetService {

    @Autowired
    private SweetRepository sweetRepository;

    public List<Sweet> getAllSweets(String name, String category, Double minPrice, Double maxPrice) {
        // If any search parameters are provided, use the filtered search
        if (name != null || category != null || minPrice != null || maxPrice != null) {
            List<Sweet> result = new ArrayList<>();
            
            // Start with all sweets and filter
            List<Sweet> allSweets = sweetRepository.findAll();
            
            for (Sweet sweet : allSweets) {
                boolean matches = true;
                
                // Filter by name
                if (name != null && !name.isEmpty()) {
                    matches = matches && sweet.getName() != null && 
                             sweet.getName().toLowerCase().contains(name.toLowerCase());
                }
                
                // Filter by category
                if (category != null && !category.isEmpty()) {
                    matches = matches && sweet.getCategory() != null && 
                             sweet.getCategory().toLowerCase().contains(category.toLowerCase());
                }
                
                // Filter by price range
                if (minPrice != null) {
                    matches = matches && sweet.getPrice() != null && sweet.getPrice() >= minPrice;
                }
                
                if (maxPrice != null) {
                    matches = matches && sweet.getPrice() != null && sweet.getPrice() <= maxPrice;
                }
                
                if (matches) {
                    result.add(sweet);
                }
            }
            
            return result;
        }
        // Otherwise, return all sweets
        return sweetRepository.findAll();
    }

    public Sweet getSweetById(UUID id) {
        return sweetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sweet not found with id: " + id));
    }

    public Sweet createSweet(Sweet sweet) {
        return sweetRepository.save(sweet);
    }

    public Sweet updateSweet(UUID id, Sweet sweetDetails) {
        Sweet sweet = getSweetById(id);
        
        sweet.setName(sweetDetails.getName());
        sweet.setCategory(sweetDetails.getCategory());
        sweet.setPrice(sweetDetails.getPrice());
        sweet.setQuantity(sweetDetails.getQuantity());
        sweet.setDescription(sweetDetails.getDescription());
        sweet.setImage(sweetDetails.getImage());
        
        return sweetRepository.save(sweet);
    }

    public void deleteSweet(UUID id) {
        Sweet sweet = getSweetById(id);
        sweetRepository.delete(sweet);
    }


    @Transactional
    public Sweet purchaseSweet(UUID id, Integer quantity) {
        try {
            Sweet sweet = getSweetById(id);
            
            if (sweet.getQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock available for sweet: " + sweet.getName() + 
                    ". Available: " + sweet.getQuantity() + ", Requested: " + quantity);
            }
            
            sweet.setQuantity(sweet.getQuantity() - quantity);
            return sweetRepository.save(sweet);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Sweet not found with id: " + id);
        } catch (Exception e) {
            throw new RuntimeException("Error purchasing sweet: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Sweet restockSweet(UUID id, Integer quantity) {
        Sweet sweet = getSweetById(id);
        sweet.setQuantity(sweet.getQuantity() + quantity);
        return sweetRepository.save(sweet);
    }
}
