package com.vault.secure_cloud_vault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final RestTemplate restTemplate = new RestTemplate();

    // Upload file to Supabase
    public String uploadFile(MultipartFile file) throws IOException {

        // Generate unique file name
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Build upload URL
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + uniqueFileName;

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.setContentType(MediaType.parseMediaType(
                file.getContentType() != null ? file.getContentType() : "application/octet-stream"
        ));

        // Create request
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        // Upload to Supabase
        restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);

        return uniqueFileName;
    }

    // Download file from Supabase
    public byte[] downloadFile(String fileName) {

        // Build download URL
        String downloadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

        System.out.println("Downloading from URL: " + downloadUrl);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Download from Supabase
        ResponseEntity<byte[]> response = restTemplate.exchange(
                downloadUrl,
                HttpMethod.GET,
                requestEntity,
                byte[].class
        );

        return response.getBody();
    }

    // Delete file from Supabase
    public void deleteFile(String fileName) {

        // Build delete URL
        String deleteUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Delete from Supabase
        restTemplate.exchange(deleteUrl, HttpMethod.DELETE, requestEntity, String.class);
    }
}