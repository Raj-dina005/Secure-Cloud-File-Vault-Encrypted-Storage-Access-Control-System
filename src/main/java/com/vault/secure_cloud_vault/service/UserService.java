package com.vault.secure_cloud_vault.service;

import com.vault.secure_cloud_vault.entity.User;
import com.vault.secure_cloud_vault.repository.UserRepository;
import com.vault.secure_cloud_vault.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.vault.secure_cloud_vault.entity.FileMetadata;
import com.vault.secure_cloud_vault.repository.FileRepository;
import com.vault.secure_cloud_vault.service.StorageService;
import java.util.List;
import com.vault.secure_cloud_vault.dto.AuthRequest;
import com.vault.secure_cloud_vault.dto.AuthResponse;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final FileRepository fileRepository;
    private final StorageService storageService;

    // Register new user
    public String register(String fullName, String email, String password) {

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered!");
        }

        // Create new user
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // encrypt password
        user.setRole(User.Role.USER);

        // Save to database
        userRepository.save(user);

        // Generate and return JWT token
        return jwtUtil.generateToken(email, User.Role.USER.name());
    }

    // Login existing user
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with this email!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect password!");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), "Login successful!");
    }

    // Required by Spring Security - loads user by email
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }


    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Delete all files from Supabase
        List<FileMetadata> files = fileRepository.findByUploadedBy(user);
        for (FileMetadata file : files) {
            try {
                storageService.deleteFile(file.getS3Key());
            } catch (Exception e) {
                System.out.println("Could not delete from Supabase: " + e.getMessage());
            }
        }

        // Delete all DB records
        fileRepository.deleteAll(files);

        // Delete user
        userRepository.delete(user);
    }
}