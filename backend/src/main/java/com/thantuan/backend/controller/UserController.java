package com.thantuan.backend.controller;

import com.thantuan.backend.dto.Response;
import com.thantuan.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController()
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/get-info-user")
    public ResponseEntity<Response> getInfoUser() {
        return ResponseEntity.ok(userService.getInfoUser());
    }

    @PostMapping("/request-role-promotion")
    public ResponseEntity<Response> requestRolePromotion(@RequestParam Map<String, Object> requestBody)
            throws BadRequestException {
        String toRole = (String) requestBody.get("toRole");
        String fromRole = (String) requestBody.get("fromRole");
        return ResponseEntity.ok(userService.requestRolePromotion(toRole, fromRole));
    }

    @PatchMapping("/approve/{requestId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> approve(@PathVariable Long requestId,
                                            @RequestParam Map<String, Object> requestBody) {
        Boolean isApproved = (Boolean) requestBody.get("isApproved");
        String role = (String) requestBody.get("role");
        return ResponseEntity.ok(userService.approve(requestId, isApproved, role));
    }

    @PostMapping("/init-roles")
    public ResponseEntity<Response> initRoles() {
        return ResponseEntity.ok(userService.initRoles());
    }
}
