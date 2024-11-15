package com.thantuan.backend.repository;

import com.thantuan.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IProductRepo extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByNameWithoutAccentContainingIgnoreCaseOrDescriptionWithoutAccentContainingIgnoreCase
            (String nameWithoutAccent, String descriptionWithoutAccent);

    List<Product> getProductsByUserId(Long userId);
}