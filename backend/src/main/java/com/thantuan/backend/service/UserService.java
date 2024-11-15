package com.thantuan.backend.service;

import com.thantuan.backend.dto.Response;
import com.thantuan.backend.dto.UserDto;
import com.thantuan.backend.entity.Role;
import com.thantuan.backend.entity.RoleUpgradeRequest;
import com.thantuan.backend.entity.User;
import com.thantuan.backend.enums.RequestStatus;
import com.thantuan.backend.mapper.EntityDtoMapper;
import com.thantuan.backend.repository.IRoleRepo;
import com.thantuan.backend.repository.IRoleUpgradeRequestRepo;
import com.thantuan.backend.repository.IUserRepo;
import com.thantuan.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.CREATED;

@Service
@RequiredArgsConstructor
public class UserService {
    private final IUserRepo userRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final IRoleRepo roleRepo;
    private final IRoleUpgradeRequestRepo roleUpgradeRequestRepo;

    public Response getInfoUser() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        UserDto userDTO = entityDtoMapper.mapUserToUserDto(currentUser);
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Get info user successfully")
                .user(userDTO)
                .build();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user.getUser();
    }

    public Response requestRolePromotion(String toRoleRequest, String fromRoleRequest) throws BadRequestException {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (toRoleRequest.equals(fromRoleRequest)) {
            throw new BadRequestException("Can not upgrade role");
        }

        Role toRole = roleRepo.findByName(toRoleRequest)
                .orElseThrow(() -> new UsernameNotFoundException("Role not found"));
        Role fromRole = roleRepo.findByName(fromRoleRequest)
                .orElseThrow(() -> new UsernameNotFoundException("Role not found"));

        RoleUpgradeRequest roleUpgradeRequest = RoleUpgradeRequest.builder()
                            .user(currentUser)
                            .requestedRole(fromRole)
                            .currentRoleValue(toRole)
                            .status(RequestStatus.PENDING).build();
        roleUpgradeRequestRepo.save(roleUpgradeRequest);
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Request upgrade role successfully")
                .build();
    }

    public Response approve(Long requestId, boolean isApproved, String fromRoleRequest) {
        RoleUpgradeRequest roleUpgradeRequest = roleUpgradeRequestRepo.findById(requestId)
                .orElseThrow(() -> new UsernameNotFoundException("Role upgrade request " + "not found"));

        User user = userRepo.findById(roleUpgradeRequest.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<Role> currentRoles = user.getRoles();
        currentRoles.add((Role) Set.of(fromRoleRequest));

        if (isApproved) {
            roleUpgradeRequest.setStatus(RequestStatus.APPROVED);
            user.setRoles(currentRoles);
            userRepo.save(user);
            roleUpgradeRequestRepo.delete(roleUpgradeRequest);
        }

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Resolve request upgrade role successfully")
                .build();
    }

    public Response initRoles() {
        Optional<Role> roleBuyer = roleRepo.findByName("BUYER");
        if (roleBuyer.isEmpty()) {
            Role role = Role.builder().name("BUYER").build();
            roleRepo.save(role);
        }

        Optional<Role> roleSeller = roleRepo.findByName("SELLER");
        if (roleSeller.isEmpty()) {
            Role role = Role.builder().name("SELLER").build();
            roleRepo.save(role);
        }

        Optional<Role> roleAdmin = roleRepo.findByName("ADMIN");
        if (roleAdmin.isEmpty()) {
            Role role = Role.builder().name("ADMIN").build();
            roleRepo.save(role);
        }
        return Response.builder().status(CREATED.value()).message("Init roles successfully").build();
    }
}
