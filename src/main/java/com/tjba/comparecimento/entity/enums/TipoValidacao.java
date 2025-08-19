package com.tjba.comparecimento.entity.enums;

/**
 * Enum que define os tipos de validação de comparecimento.
 */
public enum TipoValidacao {

    PRESENCIAL("presencial", "Presencial", "Comparecimento físico no local"),
    ONLINE("online", "Online", "Comparecimento virtual/remoto"),
    JUSTIFICADO("justificado", "Justificado", "Ausência justificada documentalmente");

    private final String code;
    private final String label;
    private final String description;

    TipoValidacao(String code, String label, String description) {
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
    public static TipoValidacao fromString(String value) {
        if (value == null) return null;

        for (TipoValidacao tipo : TipoValidacao.values()) {
            if (tipo.code.equalsIgnoreCase(value) ||
                    tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de validação inválido: " + value);
    }

    /**
     * Verifica se requer presença física
     */
    public boolean requerPresencaFisica() {
        return this == PRESENCIAL;
    }

    /**
     * Verifica se é virtual
     */
    public boolean isVirtual() {
        return this == ONLINE;
    }

    /**
     * Verifica se é justificativa
     */
    public boolean isJustificativa() {
        return this == JUSTIFICADO;
    }

    /**
     * Retorna ícone baseado no tipo
     */
    public String getIcon() {
        return switch (this) {
            case PRESENCIAL -> "building";
            case ONLINE -> "monitor";
            case JUSTIFICADO -> "file-text";
        };
    }

    @Override
    public String toString() {
        return label;
    }
}
