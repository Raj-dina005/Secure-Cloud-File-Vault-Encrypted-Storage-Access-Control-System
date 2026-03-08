package com.vault.secure_cloud_vault.controller;

import com.vault.secure_cloud_vault.entity.FileMetadata;
import com.vault.secure_cloud_vault.entity.ShareLink;
import com.vault.secure_cloud_vault.service.StorageService;
import com.vault.secure_cloud_vault.service.ShareLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareLinkController {

    private final ShareLinkService shareLinkService;
    private final StorageService storageService;

    // Create a share link
    @PostMapping("/create/{fileId}")
    public ResponseEntity<ShareLink> createShareLink(
            @PathVariable Long fileId,
            @RequestParam(defaultValue = "24") int expiryHours,
            @RequestParam(defaultValue = "false") boolean oneTimeUse,
            Authentication authentication) {

        String userEmail = authentication.getName();
        ShareLink shareLink = shareLinkService.createShareLink(
                fileId,
                userEmail,
                expiryHours,
                oneTimeUse
        );
        return ResponseEntity.ok(shareLink);
    }

    // Access file via share link token (public - no auth needed)
    @GetMapping("/{token}")
    public ResponseEntity<byte[]> accessShareLink(@PathVariable String token) {
        try {
            FileMetadata file = shareLinkService.accessShareLink(token);
            byte[] fileBytes = storageService.downloadFile(file.getS3Key());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(fileBytes);
        } catch (RuntimeException e) {
            System.out.println("Share link error: " + e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }
}