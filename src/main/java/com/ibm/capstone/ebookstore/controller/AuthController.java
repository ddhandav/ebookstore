package com.ibm.capstone.ebookstore.controller;

import com.ibm.capstone.ebookstore.dto.ApiResponse;
import com.ibm.capstone.ebookstore.dto.AuthResponse;
import com.ibm.capstone.ebookstore.dto.LoginRequest;
import com.ibm.capstone.ebookstore.dto.RegisterRequest;
import com.ibm.capstone.ebookstore.entity.User;
import com.ibm.capstone.ebookstore.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration, authentication, and profile APIs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Registers customer and grants 500 bonus gift points")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest req) {
        AuthResponse response = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest req) {
        AuthResponse response = authService.login(req);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @GetMapping("/profile/{userId}")
    @Operation(summary = "Get user profile", description = "Retrieves user details and gift points balance")
    public ResponseEntity<ApiResponse<User>> getProfile(@PathVariable Long userId) {
        User user = authService.getUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }
}
