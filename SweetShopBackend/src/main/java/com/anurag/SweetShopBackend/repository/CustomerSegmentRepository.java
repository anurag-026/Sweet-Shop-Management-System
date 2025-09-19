package com.anurag.SweetShopBackend.repository;

import com.anurag.SweetShopBackend.model.CustomerSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerSegmentRepository extends JpaRepository<CustomerSegment, UUID> {
    Optional<CustomerSegment> findBySegmentName(String segmentName);
}
