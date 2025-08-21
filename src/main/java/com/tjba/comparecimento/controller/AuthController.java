package com.tjba.comparecimento.controller;

import com.tjba.comparecimento.dto.request.LoginRequest;
import com.tjba.comparecimento.dto.request.RefreshTokenRequest;
import com.tjba.comparecimento.dto.response.ApiResponse;
import com.tjba.comparecimento.dto.response.LoginResponse;
import com.tjba.comparecimento.dto.response.TokenResponse;
import com.tjba.comparecimento.dto.response.UserInfoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticação e autorização.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AuthController {

    // TODO: Injetar AuthService quando implementar
    // @Autowired private AuthService authService;

    /**
     * Endpoint para login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        // TODO: authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Login realizado com sucesso"));
    }

    /**
     * Endpoint para logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        // TODO: authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logout realizado com sucesso"));
    }

    /**
     * Endpoint para renovar token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        // TODO: authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Token renovado com sucesso"));
    }

    /**
     * Endpoint para validar token
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<UserInfoResponse>> validateToken(@RequestHeader("Authorization") String token) {
        // TODO: authService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}