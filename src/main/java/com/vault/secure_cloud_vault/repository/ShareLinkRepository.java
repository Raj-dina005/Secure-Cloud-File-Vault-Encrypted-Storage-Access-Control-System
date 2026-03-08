package com.vault.secure_cloud_vault.repository;

import com.vault.secure_cloud_vault.entity.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ShareLinkRepository extends JpaRepository<ShareLink, Long> {

    // Find share link by token
    Optional<ShareLink> findByToken(String token);
}