package com.vault.secure_cloud_vault.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String fullName;
    private String email;
    private String password;
}