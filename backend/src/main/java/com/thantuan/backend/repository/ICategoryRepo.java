package com.thantuan.backend.repository;

import com.thantuan.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICategoryRepo extends JpaRepository<Category, Long> {
    List<Category> findByNameIn(List<String> roles);
    Category findByName(String name);
}
