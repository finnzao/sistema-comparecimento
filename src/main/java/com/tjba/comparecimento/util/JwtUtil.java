package com.tjba.comparecimento.util;

import com.tjba.comparecimento.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário para geração e validação de tokens JWT.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:defaultSecretKeyForJWTTokenThatShouldBeVeryLongAndSecure123456789}")
    private String secret;

    @Value("${jwt.access-token-expiration:3600000}") // 1 hora em ms
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}") // 7 dias em ms
    private Long refreshTokenExpiration;

    @Value("${jwt.password-reset-expiration:1800000}") // 30 minutos em ms
    private Long passwordResetExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Gerar token de acesso
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().getCode());
        claims.put("nome", user.getNome());
        claims.put("departamento", user.getDepartamento());
        claims.put("tokenType", "access");

        return createToken(claims, user.getEmail(), accessTokenExpiration);
    }

    /**
     * Gerar refresh token
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("tokenType", "refresh");

        return createToken(claims, user.getEmail(), refreshTokenExpiration);
    }

    /**
     * Gerar token para reset de senha
     */
    public String generatePasswordResetToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("tokenType", "password_reset");

        return createToken(claims, user.getEmail(), passwordResetExpiration);
    }

    /**
     * Criar token JWT
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrair email do token
     */
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extrair email do refresh token
     */
    public String getEmailFromRefreshToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        String tokenType = (String) claims.get("tokenType");

        if (!"refresh".equals(tokenType)) {
            throw new IllegalArgumentException("Token não é um refresh token");
        }

        return claims.getSubject();
    }

    /**
     * Extrair email do token de reset de senha
     */
    public String getEmailFromPasswordResetToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        String tokenType = (String) claims.get("tokenType");

        if (!"password_reset".equals(tokenType)) {
            throw new IllegalArgumentException("Token não é um token de reset de senha");
        }

        return claims.getSubject();
    }

    /**
     * Extrair userId do token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * Extrair role do token
     */
    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (String) claims.get("role");
    }

    /**
     * Extrair data de expiração do token
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extrair claim específico do token
     */
    public <T> T getClaimFromToken(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.resolve(claims);
    }

    /**
     * Extrair todas as claims do token
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Token JWT inválido", e);
        }
    }

    /**
     * Verificar se o token expirou
     */
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Validar token de acesso
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String tokenType = (String) claims.get("tokenType");

            return "access".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validar refresh token
     */
    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String tokenType = (String) claims.get("tokenType");

            return "refresh".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validar token de reset de senha
     */
    public boolean isPasswordResetTokenValid(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String tokenType = (String) claims.get("tokenType");

            return "password_reset".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obter tempo de expiração do access token em segundos
     */
    public Long getAccessTokenExpiration() {
        return accessTokenExpiration / 1000; // Converter para segundos
    }

    /**
     * Obter tempo de expiração do refresh token em segundos
     */
    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration / 1000; // Converter para segundos
    }

    /**
     * Extrair informações do usuário do token
     */
    public UserTokenInfo getUserInfoFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        return new UserTokenInfo(
                Long.valueOf(claims.get("userId").toString()),
                claims.getSubject(),
                (String) claims.get("role"),
                (String) claims.get("nome"),
                (String) claims.get("departamento")
        );
    }

    // === INTERFACE FUNCIONAL ===
    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }

    // === CLASSE AUXILIAR ===
    public static class UserTokenInfo {
        private final Long userId;
        private final String email;
        private final String role;
        private final String nome;
        private final String departamento;

        public UserTokenInfo(Long userId, String email, String role, String nome, String departamento) {
            this.userId = userId;
            this.email = email;
            this.role = role;
            this.nome = nome;
            this.departamento = departamento;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getNome() { return nome; }
        public String getDepartamento() { return departamento; }
    }
}