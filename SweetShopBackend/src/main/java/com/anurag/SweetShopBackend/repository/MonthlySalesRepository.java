package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.MonthlySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MonthlySalesRepository extends JpaRepository<MonthlySales, UUID> {
    Optional<MonthlySales> findByMonthAndYear(Integer month, Integer year);
    
    List<MonthlySales> findByYearOrderByMonth(Integer year);
    
    @Query("SELECT ms FROM MonthlySales ms ORDER BY ms.year DESC, ms.month DESC LIMIT ?1")
    List<MonthlySales> findRecentMonths(Integer limit);
}
