package com.thantuan.backend.repository;

import com.thantuan.backend.entity.RoleUpgradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleUpgradeRequestRepo extends JpaRepository<RoleUpgradeRequest, Long> {
}
