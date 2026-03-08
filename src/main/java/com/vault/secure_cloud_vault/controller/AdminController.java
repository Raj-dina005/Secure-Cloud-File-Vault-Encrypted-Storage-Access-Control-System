package com.vault.secure_cloud_vault.controller;

import com.vault.secure_cloud_vault.entity.User;
import com.vault.secure_cloud_vault.repository.UserRepository;
import com.vault.secure_cloud_vault.service.FileService;
import com.vault.secure_cloud_vault.service.StorageService;
import com.vault.secure_cloud_vault.repository.FileRepository;
import com.vault.secure_cloud_vault.entity.FileMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final StorageService storageService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete all files from Supabase and DB
        List<FileMetadata> files = fileRepository.findByUploadedBy(user);
        for (FileMetadata file : files) {
            try {
                storageService.deleteFile(file.getS3Key());
            } catch (Exception e) {
                System.out.println("Could not delete from Supabase: " + e.getMessage());
            }
        }
        fileRepository.deleteAll(files);

        // Delete user
        userRepository.delete(user);

        return ResponseEntity.ok("User deleted successfully!");
    }
}