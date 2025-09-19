package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.CategorySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategorySalesRepository extends JpaRepository<CategorySales, UUID> {
    Optional<CategorySales> findByCategoryAndTimePeriodAndPeriodStart(
        String category, String timePeriod, LocalDate periodStart);
    
    List<CategorySales> findByTimePeriodAndPeriodStartOrderByRevenueDesc(
        String timePeriod, LocalDate periodStart);
    
    List<CategorySales> findByTimePeriodAndPeriodStartBetweenOrderByRevenueDesc(
        String timePeriod, LocalDate startDate, LocalDate endDate);

    List<CategorySales> findByTimePeriodAndPeriodStart(String timePeriod, LocalDate periodStart);
}
