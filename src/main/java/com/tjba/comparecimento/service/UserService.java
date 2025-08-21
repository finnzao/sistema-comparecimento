package com.tjba.comparecimento.service;

import com.tjba.comparecimento.dto.request.CreateUserRequest;
import com.tjba.comparecimento.dto.request.UpdateUserRequest;
import com.tjba.comparecimento.dto.response.UserResponse;
import com.tjba.comparecimento.entity.User;
import com.tjba.comparecimento.entity.enums.UserRole;
import com.tjba.comparecimento.exception.BusinessException;
import com.tjba.comparecimento.exception.ResourceNotFoundException;
import com.tjba.comparecimento.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service para gerenciamento de usuários.
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // TODO: Injetar EmailService quando implementar
    // @Autowired private EmailService emailService;

    // TODO: Injetar AuditService quando implementar
    // @Autowired private AuditService auditService;

    /**
     * Buscar usuários com filtros e paginação
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> findAllWithFilters(int page, int size, String sortBy, String sortDir,
                                                 String nome, String email, UserRole role, String departamento) {

        // 1. Configurar paginação e ordenação
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 2. Buscar com filtros
        Page<User> usersPage = userRepository.findAllWithFilters(nome, email, role, departamento, pageable);

        // 3. Converter para DTO
        return usersPage.map(this::convertToUserResponse);
    }

    /**
     * Buscar usuário por ID
     */
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        return convertToUserResponse(user);
    }

    /**
     * Buscar usuário por email
     */
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));

        return convertToUserResponse(user);
    }

    /**
     * Criar novo usuário
     */
    public UserResponse createUser(CreateUserRequest request) {
        // 1. Validar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Já existe um usuário com este email");
        }

        // 2. Validar dados
        validateUserData(request.getNome(), request.getEmail(), request.getPassword());

        // 3. Criar entidade
        User user = new User();
        user.setNome(request.getNome().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setDepartamento(request.getDepartamento());
        user.setTelefone(request.getTelefone());
        user.setAtivo(true);

        // 4. Salvar usuário
        User savedUser = userRepository.save(user);

        // 5. Enviar email de boas-vindas
        // TODO: emailService.sendWelcomeEmail(savedUser.getEmail(), request.getPassword());

        // 6. Log da ação
        // TODO: auditService.logUserCreation(savedUser.getId(), savedUser.getEmail());

        return convertToUserResponse(savedUser);
    }

    /**
     * Atualizar usuário existente
     */
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        // 1. Buscar usuário
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        // 2. Validar se email já existe em outro usuário
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Já existe outro usuário com este email");
        }

        // 3. Validar dados
        validateUserData(request.getNome(), request.getEmail(), null);

        // 4. Atualizar campos
        user.setNome(request.getNome().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setRole(request.getRole());
        user.setDepartamento(request.getDepartamento());
        user.setTelefone(request.getTelefone());

        if (request.getAtivo() != null) {
            user.setAtivo(request.getAtivo());
        }

        // 5. Salvar alterações
        User updatedUser = userRepository.save(user);

        // 6. Log da ação
        // TODO: auditService.logUserUpdate(updatedUser.getId(), updatedUser.getEmail());

        return convertToUserResponse(updatedUser);
    }

    /**
     * Desativar usuário (soft delete)
     */
    public void deactivateUser(Long id) {
        // 1. Buscar usuário
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        // 2. Verificar se não é o último admin ativo
        if (user.getRole().isAdmin()) {
            long activeAdmins = userRepository.countByRoleAndAtivo(UserRole.ADMIN, true);
            if (activeAdmins <= 1) {
                throw new BusinessException("Não é possível desativar o último administrador ativo");
            }
        }

        // 3. Desativar usuário
        user.setAtivo(false);
        userRepository.save(user);

        // 4. Log da ação
        // TODO: auditService.logUserDeactivation(user.getId(), user.getEmail());
    }

    /**
     * Reativar usuário
     */
    public void reactivateUser(Long id) {
        // 1. Buscar usuário (incluindo inativos)
        User user = userRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        // 2. Verificar se email ainda está disponível
        if (userRepository.existsByEmailAndAtivoTrue(user.getEmail())) {
            throw new BusinessException("Já existe um usuário ativo com este email");
        }

        // 3. Reativar usuário
        user.setAtivo(true);
        userRepository.save(user);

        // 4. Log da ação
        // TODO: auditService.logUserReactivation(user.getId(), user.getEmail());
    }

    /**
     * Resetar senha do usuário
     */
    public void resetPassword(Long id) {
        // 1. Buscar usuário
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        // 2. Gerar nova senha temporária
        String newPassword = generateTemporaryPassword();

        // 3. Atualizar senha
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 4. Enviar nova senha por email
        // TODO: emailService.sendPasswordResetEmail(user.getEmail(), newPassword);

        // 5. Log da ação
        // TODO: auditService.logPasswordReset(user.getId(), user.getEmail());
    }

    /**
     * Atualizar avatar do usuário
     */
    public void updateAvatar(Long id, String avatarUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        user.setAvatar(avatarUrl);
        userRepository.save(user);
    }

    /**
     * Atualizar configurações do usuário
     */
    public void updateConfiguracoes(Long id, String configuracoes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        user.setConfiguracoes(configuracoes);
        userRepository.save(user);
    }

    /**
     * Verificar se usuário existe por email
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Contar usuários por role
     */
    @Transactional(readOnly = true)
    public long countByRole(UserRole role) {
        return userRepository.countByRoleAndAtivo(role, true);
    }

    /**
     * Buscar usuários ativos por departamento
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> findByDepartamento(String departamento, Pageable pageable) {
        Page<User> users = userRepository.findByDepartamentoAndAtivoTrue(departamento, pageable);
        return users.map(this::convertToUserResponse);
    }

    // === MÉTODOS AUXILIARES ===

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getRole(),
                user.getDepartamento(),
                user.getTelefone(),
                user.getAvatar(),
                user.getAtivo(),
                user.getUltimoLogin(),
                user.getCriadoEm(),
                user.getAtualizadoEm()
        );
    }

    private void validateUserData(String nome, String email, String password) {
        // Validar nome
        if (nome == null || nome.trim().length() < 2) {
            throw new BusinessException("Nome deve ter pelo menos 2 caracteres");
        }

        if (nome.trim().length() > 100) {
            throw new BusinessException("Nome deve ter no máximo 100 caracteres");
        }

        // Validar email
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException("Email deve ter formato válido");
        }

        // Validar senha (apenas na criação)
        if (password != null) {
            if (password.length() < 6) {
                throw new BusinessException("Senha deve ter pelo menos 6 caracteres");
            }

            if (password.length() > 50) {
                throw new BusinessException("Senha deve ter no máximo 50 caracteres");
            }
        }
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}