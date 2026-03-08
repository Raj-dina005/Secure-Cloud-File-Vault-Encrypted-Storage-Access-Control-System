package com.vault.secure_cloud_vault.service;

import com.vault.secure_cloud_vault.entity.AuditLog;
import com.vault.secure_cloud_vault.entity.FileMetadata;
import com.vault.secure_cloud_vault.entity.User;
import com.vault.secure_cloud_vault.repository.FileRepository;
import com.vault.secure_cloud_vault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final StorageService storageService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Upload a file
    public FileMetadata uploadFile(MultipartFile file, String userEmail) throws IOException {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Upload file to Supabase
        String uniqueFileName = storageService.uploadFile(file);

        // Save file metadata to database
        FileMetadata metadata = new FileMetadata();
        metadata.setFileName(file.getOriginalFilename());
        metadata.setFileType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadata.setS3Key(uniqueFileName);
        metadata.setUploadedBy(user);
        metadata.setStatus(FileMetadata.FileStatus.ACTIVE);

        FileMetadata savedMetadata = fileRepository.save(metadata);
        auditLogService.log(userEmail, savedMetadata, AuditLog.Action.UPLOAD, "File uploaded: " + file.getOriginalFilename(), "127.0.0.1");
        return savedMetadata;
    }

    // Get all files for a user
    public List<FileMetadata> getUserFiles(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return fileRepository.findByUploadedByAndStatus(
                user,
                FileMetadata.FileStatus.ACTIVE
        );
    }

    // Get all files (admin only)
    public List<FileMetadata> getAllFiles() {
        return fileRepository.findByStatus(FileMetadata.FileStatus.ACTIVE);
    }

    // Download a file
    public byte[] downloadFile(Long fileId, String userEmail) {
        FileMetadata metadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found!"));

        if (!metadata.getUploadedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied!");
        }

        auditLogService.log(userEmail, metadata, AuditLog.Action.DOWNLOAD, "File downloaded: " + metadata.getFileName(), "127.0.0.1");
        return storageService.downloadFile(metadata.getS3Key());
    }

    // Soft delete a file
    public void deleteFile(Long fileId, String userEmail) {
        FileMetadata metadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found!"));

        if (!metadata.getUploadedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied!");
        }

        metadata.setStatus(FileMetadata.FileStatus.DELETED);
        auditLogService.log(userEmail, metadata, AuditLog.Action.DELETE, "File deleted: " + metadata.getFileName(), "127.0.0.1");
        fileRepository.save(metadata);
    }

    // Get file metadata
    public FileMetadata getFileMetadata(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found!"));
    }

    // Get file path from metadata
    public Path getFilePath(FileMetadata metadata) {
        return Paths.get(uploadDir).resolve(metadata.getS3Key());
    }
}