package com.tjba.comparecimento.entity.enums;

/**
 * Enum que define os status de comparecimento de uma pessoa.
 */
public enum StatusComparecimento {

    EM_CONFORMIDADE("em conformidade", "Em Conformidade"),
    INADIMPLENTE("inadimplente", "Inadimplente");

    private final String code;
    private final String label;

    StatusComparecimento(String code, String label) {
        this.code = code;
        this.label = label;

    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }


    /**
     * Converte string para enum (case insensitive)
     */
    public static StatusComparecimento fromString(String value) {
        if (value == null) return null;

        for (StatusComparecimento status : StatusComparecimento.values()) {
            if (status.code.equalsIgnoreCase(value) ||
                    status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + value);
    }

    /**
     * Verifica se está em conformidade
     */
    public boolean isEmConformidade() {
        return this == EM_CONFORMIDADE;
    }

    /**
     * Verifica se é inadimplente
     */
    public boolean isInadimplente() {
        return this == INADIMPLENTE;
    }

    /**
     * Retorna cor CSS baseada no status
     */
    public String getCssClass() {
        return switch (this) {
            case EM_CONFORMIDADE -> "success";
            case INADIMPLENTE -> "danger";
        };
    }

    @Override
    public String toString() {
        return label;
    }
}
