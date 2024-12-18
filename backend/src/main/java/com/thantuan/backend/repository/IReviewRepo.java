package com.thantuan.backend.repository;

import com.thantuan.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
}
