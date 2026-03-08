package com.vault.secure_cloud_vault.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User performedBy;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileMetadata file;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    private String details;

    private String ipAddress;

    private LocalDateTime performedAt = LocalDateTime.now();

    public enum Action {
        UPLOAD, DOWNLOAD, DELETE, SHARE, LOGIN, LOGOUT
    }
}