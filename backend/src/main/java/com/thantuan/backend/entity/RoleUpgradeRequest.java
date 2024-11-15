package com.thantuan.backend.entity;

import com.thantuan.backend.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_upgrade_requests")
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleUpgradeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne()
    @JoinColumn(name = "current_role_value")
    private Role currentRoleValue;

    @OneToOne()
    @JoinColumn(name = "request_role")
    private Role requestedRole;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
