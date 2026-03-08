package com.vault.secure_cloud_vault.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "files")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    private Long fileSize;

    @Column(nullable = false)
    private String s3Key;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User uploadedBy;

    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private FileStatus status = FileStatus.ACTIVE;

    public enum FileStatus {
        ACTIVE, DELETED
    }
}