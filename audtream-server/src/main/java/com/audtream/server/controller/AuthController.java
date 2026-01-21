package com.audtream.server.controller;

import com.audtream.server.model.dto.AuthRequest;
import com.audtream.server.model.dto.AuthResponse;
import com.audtream.server.model.dto.RegisterRequest;
import com.audtream.server.model.entity.User;
import com.audtream.server.service.AuthService;
import com.audtream.server.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.authenticate(authRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = userService.registerUser(registerRequest);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(registerRequest.getUsername());
        authRequest.setPassword(registerRequest.getPassword());

        AuthResponse response = authService.authenticate(authRequest);
        return ResponseEntity.ok(response);
    }
}