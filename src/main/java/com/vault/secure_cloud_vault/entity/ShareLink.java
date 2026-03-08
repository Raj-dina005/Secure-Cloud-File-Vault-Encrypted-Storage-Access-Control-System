package com.vault.secure_cloud_vault.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "share_links")
public class ShareLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private FileMetadata file;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean oneTimeUse = false;

    private boolean used = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}