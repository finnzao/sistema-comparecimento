package com.tjba.comparecimento.controller;

import com.tjba.comparecimento.dto.request.CreateUserRequest;
import com.tjba.comparecimento.dto.request.UpdateUserRequest;
import com.tjba.comparecimento.dto.response.ApiResponse;
import com.tjba.comparecimento.dto.response.UserResponse;
import com.tjba.comparecimento.entity.enums.UserRole;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para gerenciamento de usuários.
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class UserController {

    // TODO: Injetar UserService quando implementar
    // @Autowired private UserService userService;

    /**
     * Listar usuários com paginação e filtros
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) String departamento) {

        // TODO: userService.findAllWithFilters(...);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Buscar usuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        // TODO: userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Criar novo usuário
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        // TODO: userService.createUser(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created(null, "Usuário criado com sucesso"));
    }

    /**
     * Atualizar usuário
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        // TODO: userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Usuário atualizado com sucesso"));
    }

    /**
     * Desativar usuário
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable Long id) {
        // TODO: userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("Usuário desativado com sucesso"));
    }

    /**
     * Reativar usuário
     */
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<ApiResponse<String>> reactivateUser(@PathVariable Long id) {
        // TODO: userService.reactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("Usuário reativado com sucesso"));
    }

    /**
     * Resetar senha
     */
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@PathVariable Long id) {
        // TODO: userService.resetPassword(id);
        return ResponseEntity.ok(ApiResponse.success("Nova senha enviada por email"));
    }
}