package com.tjba.comparecimento.entity.enums;

/**
 * Enum que define os tipos de usuário do sistema.
 */
public enum UserRole {

    ADMIN("admin", "Administrador", "Acesso completo ao sistema"),
    USUARIO("usuario", "Usuário", "Acesso limitado para consulta e registro de comparecimentos");

    private final String code;
    private final String label;
    private final String description;

    UserRole(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Converte string para enum (case insensitive)
     */
    public static UserRole fromString(String value) {
        if (value == null) return null;

        for (UserRole role : UserRole.values()) {
            if (role.code.equalsIgnoreCase(value) ||
                    role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role inválido: " + value);
    }

    /**
     * Verifica se é administrador
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Verifica se é usuário comum
     */
    public boolean isUsuario() {
        return this == USUARIO;
    }

    @Override
    public String toString() {
        return label;
    }
}
