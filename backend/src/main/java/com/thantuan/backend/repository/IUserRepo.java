package com.thantuan.backend.repository;

import com.thantuan.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepo extends JpaRepository<User, Long> {
    User findByEmail(String username);
}
