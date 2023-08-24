package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
