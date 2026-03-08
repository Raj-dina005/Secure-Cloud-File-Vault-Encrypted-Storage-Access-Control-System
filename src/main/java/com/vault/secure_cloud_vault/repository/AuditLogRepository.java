package com.vault.secure_cloud_vault.repository;

import com.vault.secure_cloud_vault.entity.AuditLog;
import com.vault.secure_cloud_vault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Get all logs for a specific user (newest first)
    List<AuditLog> findByPerformedByOrderByPerformedAtDesc(User user);

    // Get all logs for admin (newest first)
    List<AuditLog> findAllByOrderByPerformedAtDesc();
}