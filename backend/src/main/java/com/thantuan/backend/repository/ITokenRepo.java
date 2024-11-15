package com.thantuan.backend.repository;

import com.thantuan.backend.entity.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ITokenRepo extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM Token t WHERE t.user.id = :userId")
    void deleteByUserId(Long userId);
}
