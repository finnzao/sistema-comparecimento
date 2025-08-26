package com.tjba.comparecimento.util;

/**
 * Utilitário para validação e formatação de CPF.
 */
public final class CpfUtil {

    private CpfUtil() {
        // Classe utilitária - construtor privado
    }

    /**
     * Normaliza o CPF removendo caracteres especiais
     */
    public static String normalize(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]", "");
    }

    /**
     * Valida se o CPF é válido
     */
    public static boolean isValid(String cpf) {
        if (cpf == null) {
            return false;
        }

        String normalized = normalize(cpf);

        // Verifica se tem 11 dígitos
        if (normalized.length() != 11) {
            return false;
        }

        if (normalized.matches("(\\d)\\1{10}")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (normalized.charAt(i) - '0') * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) {
            firstDigit = 0;
        }

        // Calcula o segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (normalized.charAt(i) - '0') * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) {
            secondDigit = 0;
        }

        // Verifica se os dígitos calculados são iguais aos informados
        return (normalized.charAt(9) - '0') == firstDigit &&
                (normalized.charAt(10) - '0') == secondDigit;
    }

    /**
     * Formata o CPF com pontos e hífen
     */
    public static String format(String cpf) {
        if (cpf == null) {
            return null;
        }

        String normalized = normalize(cpf);
        if (normalized.length() != 11) {
            return cpf; // Retorna original se inválido
        }

        return normalized.substring(0, 3) + "." +
                normalized.substring(3, 6) + "." +
                normalized.substring(6, 9) + "-" +
                normalized.substring(9);
    }
}
