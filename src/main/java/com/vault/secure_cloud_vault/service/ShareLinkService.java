package com.vault.secure_cloud_vault.service;

import com.vault.secure_cloud_vault.entity.FileMetadata;
import com.vault.secure_cloud_vault.entity.ShareLink;
import com.vault.secure_cloud_vault.entity.User;
import com.vault.secure_cloud_vault.repository.FileRepository;
import com.vault.secure_cloud_vault.repository.ShareLinkRepository;
import com.vault.secure_cloud_vault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareLinkService {

    private final ShareLinkRepository shareLinkRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    // Create a share link for a file
    public ShareLink createShareLink(Long fileId,
                                     String userEmail,
                                     int expiryHours,
                                     boolean oneTimeUse) {

        // Get the file
        FileMetadata file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found!"));

        // Get the user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if user owns this file
        if (!file.getUploadedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied!");
        }

        // Generate unique token
        String token = UUID.randomUUID().toString();

        // Create share link
        ShareLink shareLink = new ShareLink();
        shareLink.setToken(token);
        shareLink.setFile(file);
        shareLink.setCreatedBy(user);
        shareLink.setExpiresAt(LocalDateTime.now().plusHours(expiryHours));
        shareLink.setOneTimeUse(oneTimeUse);
        shareLink.setUsed(false);

        return shareLinkRepository.save(shareLink);
    }

    // Validate and get file from share link token
    public FileMetadata accessShareLink(String token) {

        // Find the share link
        ShareLink shareLink = shareLinkRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid share link!"));

        // Check if link has expired
        if (LocalDateTime.now().isAfter(shareLink.getExpiresAt())) {
            throw new RuntimeException("Share link has expired!");
        }

        // Check if one-time link has already been used
        if (shareLink.isOneTimeUse() && shareLink.isUsed()) {
            throw new RuntimeException("Share link has already been used!");
        }

        // Mark as used if one-time link
        if (shareLink.isOneTimeUse()) {
            shareLink.setUsed(true);
            shareLinkRepository.save(shareLink);
        }

        return shareLink.getFile();
    }
}