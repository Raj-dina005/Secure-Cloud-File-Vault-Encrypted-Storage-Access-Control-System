package com.vault.secure_cloud_vault.controller;

import com.vault.secure_cloud_vault.entity.FileMetadata;
import com.vault.secure_cloud_vault.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // Upload file
    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        String userEmail = authentication.getName();
        FileMetadata metadata = fileService.uploadFile(file, userEmail);
        return ResponseEntity.ok(metadata);
    }

    // Get all files for logged in user
    @GetMapping
    public ResponseEntity<List<FileMetadata>> getMyFiles(
            Authentication authentication) {

        String userEmail = authentication.getName();
        List<FileMetadata> files = fileService.getUserFiles(userEmail);
        return ResponseEntity.ok(files);
    }

    // Get all files (admin only)
    @GetMapping("/admin/all")
    public ResponseEntity<List<FileMetadata>> getAllFiles() {
        List<FileMetadata> files = fileService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    // Download file
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable Long id,
            Authentication authentication) throws IOException {

        String userEmail = authentication.getName();
        byte[] fileBytes = fileService.downloadFile(id, userEmail);
        FileMetadata metadata = fileService.getFileMetadata(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + metadata.getFileName() + "\"")
                .body(fileBytes);
    }

    // Delete file
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFile(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();
        fileService.deleteFile(id, userEmail);
        return ResponseEntity.ok("File deleted successfully!");
    }
}