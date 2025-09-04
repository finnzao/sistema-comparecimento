package com.tjba.comparecimento.controller;

import com.tjba.comparecimento.dto.request.LoginRequest;
import com.tjba.comparecimento.dto.request.RefreshTokenRequest;
import com.tjba.comparecimento.dto.response.ApiResponse;
import com.tjba.comparecimento.dto.response.LoginResponse;
import com.tjba.comparecimento.dto.response.TokenResponse;
import com.tjba.comparecimento.dto.response.UserInfoResponse;
import com.tjba.comparecimento.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticação e autorização.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint para login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Login realizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    /**
     * Endpoint para logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        try {
            authService.logout(token);
            return ResponseEntity.ok(ApiResponse.success("Logout realizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * Endpoint para renovar token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            TokenResponse response = authService.refreshToken(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Token renovado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    /**
     * Endpoint para validar token
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<UserInfoResponse>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            UserInfoResponse response = authService.validateToken(token);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.unauthorized(e.getMessage()));
        }
    }
}