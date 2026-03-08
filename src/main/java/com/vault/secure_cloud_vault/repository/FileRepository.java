package com.vault.secure_cloud_vault.repository;

import com.vault.secure_cloud_vault.entity.FileMetadata;
import com.vault.secure_cloud_vault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileMetadata, Long> {

    // Get all files uploaded by a specific user
    List<FileMetadata> findByUploadedBy(User user);

    // Get all active files uploaded by a specific user
    List<FileMetadata> findByUploadedByAndStatus(
            User user,
            FileMetadata.FileStatus status
    );

    // Get all active files (for admin)
    List<FileMetadata> findByStatus(FileMetadata.FileStatus status);
}