package com.tjba.comparecimento.util;

/**
 * Utilitário para validação e formatação de CEP.
 */
public class CepUtil {

    private CepUtil() {
        // Classe utilitária - construtor privado
    }

    /**
     * Normaliza o CEP removendo caracteres especiais
     */
    public static String normalize(String cep) {
        if (cep == null) {
            return null;
        }
        return cep.replaceAll("[^\\d]", "");
    }

    /**
     * Valida se o CEP é válido
     */
    public static boolean isValid(String cep) {
        if (cep == null) {
            return false;
        }

        // Normalizar CEP
        cep = normalize(cep);

        // Verificar se tem 8 dígitos
        if (cep.length() != 8) {
            return false;
        }

        // Verificar se não são todos zeros
        if (cep.equals("00000000")) {
            return false;
        }

        // Verificar se todos os dígitos são iguais
        if (cep.matches("(\\d)\\1{7}")) {
            return false;
        }

        return true;
    }

    /**
     * Formata o CEP com máscara (00000-000)
     */
    public static String format(String cep) {
        if (cep == null) {
            return null;
        }

        cep = normalize(cep);

        if (cep.length() != 8) {
            return cep; // Retorna sem formatação se não tiver 8 dígitos
        }

        return cep.substring(0, 5) + "-" + cep.substring(5);
    }

    /**
     * Remove a formatação do CEP
     */
    public static String unformat(String cep) {
        return normalize(cep);
    }

    /**
     * Verifica se o CEP pertence a uma região específica
     */
    public static String getRegiao(String cep) {
        if (!isValid(cep)) {
            return "Desconhecida";
        }

        cep = normalize(cep);
        int prefixo = Integer.parseInt(cep.substring(0, 1));

        return switch (prefixo) {
            case 0, 1 -> "Grande São Paulo";
            case 2 -> "Interior de São Paulo";
            case 3 -> "Rio de Janeiro";
            case 4 -> "Bahia";
            case 5 -> "Minas Gerais";
            case 6 -> "Distrito Federal e Goiás";
            case 7 -> "Paraná";
            case 8 -> "Santa Catarina e Rio Grande do Sul";
            case 9 -> "Mato Grosso, Mato Grosso do Sul, Rondônia, Acre, Amazonas, Roraima, Amapá e Pará";
            default -> "Região Desconhecida";
        };
    }

    /**
     * Verifica se o CEP é da Bahia
     */
    public static boolean isBahia(String cep) {
        if (!isValid(cep)) {
            return false;
        }

        cep = normalize(cep);
        return cep.startsWith("4");
    }

    /**
     * Verifica se o CEP é de Salvador
     */
    public static boolean isSalvador(String cep) {
        if (!isValid(cep)) {
            return false;
        }

        cep = normalize(cep);
        int prefixo = Integer.parseInt(cep.substring(0, 5));

        // Faixas de CEP de Salvador
        return (prefixo >= 40000 && prefixo <= 42999);
    }

    /**
     * Gera um CEP válido aleatório
     */
    public static String generateRandom() {
        StringBuilder cep = new StringBuilder();

        // Primeiro dígito (região) - evita 0 para não gerar CEPs inválidos
        cep.append((int) (Math.random() * 9) + 1);

        // Outros 7 dígitos
        for (int i = 0; i < 7; i++) {
            cep.append((int) (Math.random() * 10));
        }

        return format(cep.toString());
    }

    /**
     * Gera um CEP válido da Bahia
     */
    public static String generateRandomBahia() {
        StringBuilder cep = new StringBuilder("4");

        // Outros 7 dígitos
        for (int i = 0; i < 7; i++) {
            cep.append((int) (Math.random() * 10));
        }

        return format(cep.toString());
    }

    /**
     * Gera um CEP válido de Salvador
     */
    public static String generateRandomSalvador() {
        // CEP de Salvador: 40000-000 a 42999-999
        int base = 40000 + (int) (Math.random() * 3000);
        String sufixo = String.format("%03d", (int) (Math.random() * 1000));

        return format(base + sufixo);
    }

    /**
     * Mascara o CEP para exibição (40***-***)
     */
    public static String mask(String cep) {
        if (cep == null) {
            return null;
        }

        cep = normalize(cep);

        if (cep.length() != 8) {
            return cep;
        }

        return cep.substring(0, 2) + "***-***";
    }
}