package com.thantuan.backend.repository;

import com.thantuan.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IRoleRepo extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);

    List<Role> findByNameIn(List<String> roles);
}
