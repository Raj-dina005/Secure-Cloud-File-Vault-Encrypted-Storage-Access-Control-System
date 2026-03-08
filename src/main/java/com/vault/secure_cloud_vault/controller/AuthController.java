package com.vault.secure_cloud_vault.controller;

import com.vault.secure_cloud_vault.dto.AuthRequest;
import com.vault.secure_cloud_vault.dto.AuthResponse;
import com.vault.secure_cloud_vault.entity.User;
import com.vault.secure_cloud_vault.repository.UserRepository;
import com.vault.secure_cloud_vault.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        String token = userService.register(
                request.getFullName(),
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.ok(new AuthResponse(
                token,
                request.getEmail(),
                "USER",
                "Registration successful!"
        ));
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(Authentication authentication) {
        userService.deleteAccount(authentication.getName());
        return ResponseEntity.ok("Account deleted successfully!");
    }
}