package com.vault.secure_cloud_vault.service;

import com.vault.secure_cloud_vault.entity.AuditLog;
import com.vault.secure_cloud_vault.entity.FileMetadata;
import com.vault.secure_cloud_vault.entity.User;
import com.vault.secure_cloud_vault.repository.AuditLogRepository;
import com.vault.secure_cloud_vault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    // Log an action
    public void log(String userEmail,
                    FileMetadata file,
                    AuditLog.Action action,
                    String details,
                    String ipAddress) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        AuditLog log = new AuditLog();
        log.setPerformedBy(user);
        log.setFile(file);
        log.setAction(action);
        log.setDetails(details);
        log.setIpAddress(ipAddress);

        auditLogRepository.save(log);
    }

    // Get logs for a specific user
    public List<AuditLog> getUserLogs(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return auditLogRepository
                .findByPerformedByOrderByPerformedAtDesc(user);
    }

    // Get all logs (admin only)
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByPerformedAtDesc();
    }
}