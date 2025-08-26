package com.tjba.comparecimento.service;

import com.tjba.comparecimento.dto.request.LoginRequest;
import com.tjba.comparecimento.dto.request.RefreshTokenRequest;
import com.tjba.comparecimento.dto.response.LoginResponse;
import com.tjba.comparecimento.dto.response.TokenResponse;
import com.tjba.comparecimento.dto.response.UserInfoResponse;
import com.tjba.comparecimento.entity.User;
import com.tjba.comparecimento.repository.UserRepository;
import com.tjba.comparecimento.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service para autenticação e autorização.
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    // TODO: Implementar BlacklistService para tokens invalidados
    // @Autowired private BlacklistService blacklistService;

    /**
     * Realizar login do usuário
     */
    public LoginResponse login(LoginRequest request) {
        try {
            // 1. Autenticar credenciais
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // 2. Buscar usuário no banco
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            // 3. Verificar se usuário está ativo
            if (!user.getAtivo()) {
                throw new BadCredentialsException("Usuário desativado");
            }

            // 4. Gerar tokens JWT
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            // 5. Registrar último login
            user.setUltimoLogin(LocalDateTime.now());
            userRepository.save(user);

            // 6. Montar resposta
            UserInfoResponse userInfo = new UserInfoResponse(
                    user.getId(),
                    user.getNome(),
                    user.getEmail(),
                    user.getRole().getCode(),
                    user.getDepartamento()
            );

            return new LoginResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtUtil.getAccessTokenExpiration(),
                    userInfo
            );

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Email ou senha inválidos");
        }
    }

    /**
     * Realizar logout do usuário
     */
    public void logout(String token) {
        try {
            // 1. Extrair token do header Authorization
            String jwtToken = extractTokenFromHeader(token);

            // 2. Validar token
            if (jwtUtil.isTokenValid(jwtToken)) {
                // 3. Adicionar token à blacklist
                // TODO: blacklistService.addToBlacklist(jwtToken);

                // 4. Log da ação
                String email = jwtUtil.getEmailFromToken(jwtToken);
                // TODO: auditService.logLogout(email);
            }

        } catch (Exception e) {
            // Log do erro mas não falha o logout
            // TODO: logger.warn("Erro durante logout: {}", e.getMessage());
        }
    }

    /**
     * Renovar token de acesso
     */
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        try {
            // 1. Validar refresh token
            if (!jwtUtil.isRefreshTokenValid(request.getRefreshToken())) {
                throw new BadCredentialsException("Refresh token inválido ou expirado");
            }

            // 2. Extrair email do refresh token
            String email = jwtUtil.getEmailFromRefreshToken(request.getRefreshToken());

            // 3. Buscar usuário
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            // 4. Verificar se usuário ainda está ativo
            if (!user.getAtivo()) {
                throw new BadCredentialsException("Usuário desativado");
            }

            // 5. Gerar novo access token
            String newAccessToken = jwtUtil.generateAccessToken(user);

            return new TokenResponse(
                    newAccessToken,
                    "Bearer",
                    jwtUtil.getAccessTokenExpiration()
            );

        } catch (Exception e) {
            throw new BadCredentialsException("Erro ao renovar token: " + e.getMessage());
        }
    }

    /**
     * Validar token e retornar informações do usuário
     */
    public UserInfoResponse validateToken(String token) {
        try {
            // 1. Extrair token do header
            String jwtToken = extractTokenFromHeader(token);

            // 2. Validar token
            if (!jwtUtil.isTokenValid(jwtToken)) {
                throw new BadCredentialsException("Token inválido");
            }

            // 3. Verificar se não está na blacklist
            // TODO: if (blacklistService.isBlacklisted(jwtToken)) {
            //     throw new BadCredentialsException("Token invalidado");
            // }

            // 4. Extrair email e buscar usuário
            String email = jwtUtil.getEmailFromToken(jwtToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            // 5. Verificar se usuário ainda está ativo
            if (!user.getAtivo()) {
                throw new BadCredentialsException("Usuário desativado");
            }

            // 6. Retornar informações do usuário
            return new UserInfoResponse(
                    user.getId(),
                    user.getNome(),
                    user.getEmail(),
                    user.getRole().getCode(),
                    user.getDepartamento()
            );

        } catch (Exception e) {
            throw new BadCredentialsException("Token inválido: " + e.getMessage());
        }
    }

    /**
     * Verificar se usuário tem permissão específica
     */
    public boolean hasPermission(String email, String permission) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        // Lógica de permissões baseada no role
        return switch (permission) {
            case "ADMIN_FULL" -> user.getRole().isAdmin();
            case "USER_MANAGE" -> user.getRole().isAdmin();
            case "PESSOA_CREATE" -> user.getRole().isAdmin() || user.getRole().isUsuario();
            case "PESSOA_UPDATE" -> user.getRole().isAdmin() || user.getRole().isUsuario();
            case "COMPARECIMENTO_REGISTER" -> user.getRole().isAdmin() || user.getRole().isUsuario();
            case "RELATORIO_GENERATE" -> user.getRole().isAdmin();
            default -> false;
        };
    }

    /**
     * Trocar senha do usuário
     */
    public void changePassword(String email, String senhaAtual, String novaSenha) {
        // 1. Buscar usuário
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // 2. Validar senha atual
        if (!passwordEncoder.matches(senhaAtual, user.getPassword())) {
            throw new BadCredentialsException("Senha atual incorreta");
        }

        // 3. Validar nova senha
        validatePassword(novaSenha);

        // 4. Atualizar senha
        user.setPassword(passwordEncoder.encode(novaSenha));
        userRepository.save(user);

        // 5. Log da ação
        // TODO: auditService.logPasswordChange(email);
    }

    /**
     * Solicitar reset de senha
     */
    public void requestPasswordReset(String email) {
        // 1. Verificar se email existe
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado"));

        // 2. Gerar token de reset
        String resetToken = jwtUtil.generatePasswordResetToken(user);

        // 3. Enviar email com token
        // TODO: emailService.sendPasswordResetEmail(email, resetToken);

        // 4. Log da ação
        // TODO: auditService.logPasswordResetRequest(email);
    }

    /**
     * Confirmar reset de senha
     */
    public void confirmPasswordReset(String resetToken, String novaSenha) {
        try {
            // 1. Validar token de reset
            if (!jwtUtil.isPasswordResetTokenValid(resetToken)) {
                throw new BadCredentialsException("Token de reset inválido ou expirado");
            }

            // 2. Extrair email do token
            String email = jwtUtil.getEmailFromPasswordResetToken(resetToken);

            // 3. Buscar usuário
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            // 4. Validar nova senha
            validatePassword(novaSenha);

            // 5. Atualizar senha
            user.setPassword(passwordEncoder.encode(novaSenha));
            userRepository.save(user);

            // 6. Log da ação
            // TODO: auditService.logPasswordReset(email);

        } catch (Exception e) {
            throw new BadCredentialsException("Erro ao resetar senha: " + e.getMessage());
        }
    }



    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Header de autorização inválido");
        }
        return authHeader.substring(7);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres");
        }

        // Validações adicionais de senha
        if (!password.matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException("Senha deve conter pelo menos uma letra");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Senha deve conter pelo menos um número");
        }
    }
}