package com.vault.secure_cloud_vault.controller;

import com.vault.secure_cloud_vault.entity.AuditLog;
import com.vault.secure_cloud_vault.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogService auditLogService;

    @GetMapping("/all")
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/my")
    public ResponseEntity<List<AuditLog>> getMyLogs(
            org.springframework.security.core.Authentication authentication) {
        return ResponseEntity.ok(auditLogService.getUserLogs(authentication.getName()));
    }
}