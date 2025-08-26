package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para resposta de relatório estatístico por comarca.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelatorioEstatisticoResponse {

    private String tipoRelatorio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate periodoInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate periodoFim;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataGeracao;

    private List<Map<String, Object>> estatisticasPorComarca;
    private Map<String, Object> resumoGeral;
    private Map<String, Object> metadados;
    private List<Map<String, Object>> ranking;

    // Campos calculados
    private Integer totalComarcas;
    private Integer diasPeriodo;
    private String comarcaMaiorVolume;
    private String comarcaMaiorConformidade;
    private Double mediaComparecimentosPorComarca;

    // Constructors
    public RelatorioEstatisticoResponse() {
        this.dataGeracao = LocalDateTime.now();
    }

    public RelatorioEstatisticoResponse(String tipoRelatorio, LocalDate periodoInicio, LocalDate periodoFim,
                                        List<Map<String, Object>> estatisticasPorComarca,
                                        Map<String, Object> resumoGeral) {
        this();
        this.tipoRelatorio = tipoRelatorio;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
        this.estatisticasPorComarca = estatisticasPorComarca;
        this.resumoGeral = resumoGeral;
        calcularEstatisticasDerivadas();
    }

    /**
     * Builder pattern para facilitar construção
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RelatorioEstatisticoResponse response = new RelatorioEstatisticoResponse();

        public Builder tipoRelatorio(String tipoRelatorio) {
            response.tipoRelatorio = tipoRelatorio;
            return this;
        }

        public Builder periodoInicio(LocalDate periodoInicio) {
            response.periodoInicio = periodoInicio;
            return this;
        }

        public Builder periodoFim(LocalDate periodoFim) {
            response.periodoFim = periodoFim;
            return this;
        }

        public Builder estatisticasPorComarca(List<Map<String, Object>> estatisticasPorComarca) {
            response.estatisticasPorComarca = estatisticasPorComarca;
            return this;
        }

        public Builder resumoGeral(Map<String, Object> resumoGeral) {
            response.resumoGeral = resumoGeral;
            return this;
        }

        public Builder metadados(Map<String, Object> metadados) {
            response.metadados = metadados;
            return this;
        }

        public Builder ranking(List<Map<String, Object>> ranking) {
            response.ranking = ranking;
            return this;
        }

        public RelatorioEstatisticoResponse build() {
            response.calcularEstatisticasDerivadas();
            return response;
        }
    }

    /**
     * Calcular estatísticas derivadas automaticamente
     */
    private void calcularEstatisticasDerivadas() {
        if (estatisticasPorComarca != null) {
            this.totalComarcas = estatisticasPorComarca.size();

            // Encontrar comarca com maior volume
            this.comarcaMaiorVolume = estatisticasPorComarca.stream()
                    .max((c1, c2) -> Long.compare(
                            ((Number) c1.get("comparecimentosPeriodo")).longValue(),
                            ((Number) c2.get("comparecimentosPeriodo")).longValue()
                    ))
                    .map(comarca -> (String) comarca.get("comarca"))
                    .orElse(null);

            // Encontrar comarca com maior conformidade
            this.comarcaMaiorConformidade = estatisticasPorComarca.stream()
                    .max((c1, c2) -> Double.compare(
                            ((Number) c1.get("percentualConformidade")).doubleValue(),
                            ((Number) c2.get("percentualConformidade")).doubleValue()
                    ))
                    .map(comarca -> (String) comarca.get("comarca"))
                    .orElse(null);

            // Calcular média de comparecimentos por comarca
            Double totalComparecimentos = estatisticasPorComarca.stream()
                    .mapToDouble(comarca -> ((Number) comarca.get("comparecimentosPeriodo")).doubleValue())
                    .sum();

            if (totalComarcas > 0) {
                this.mediaComparecimentosPorComarca = totalComparecimentos / totalComarcas;
                this.mediaComparecimentosPorComarca = Math.round(mediaComparecimentosPorComarca * 100.0) / 100.0;
            }
        }

        // Calcular dias do período
        if (periodoInicio != null && periodoFim != null) {
            this.diasPeriodo = (int) periodoInicio.until(periodoFim).getDays() + 1;
        }
    }

    /**
     * Gerar ranking das comarcas por volume de comparecimentos
     */
    public List<Map<String, Object>> getRankingPorVolume() {
        if (estatisticasPorComarca == null) {
            return null;
        }

        return estatisticasPorComarca.stream()
                .sorted((c1, c2) -> Long.compare(
                        ((Number) c2.get("comparecimentosPeriodo")).longValue(),
                        ((Number) c1.get("comparecimentosPeriodo")).longValue()
                ))
                .limit(10) // Top 10
                .map(comarca -> Map.of(
                        "posicao", estatisticasPorComarca.indexOf(comarca) + 1,
                        "comarca", comarca.get("comarca"),
                        "totalComparecimentos", comarca.get("comparecimentosPeriodo"),
                        "totalPessoas", comarca.get("totalPessoas")
                ))
                .toList();
    }

    /**
     * Gerar ranking das comarcas por conformidade
     */
    public List<Map<String, Object>> getRankingPorConformidade() {
        if (estatisticasPorComarca == null) {
            return null;
        }

        return estatisticasPorComarca.stream()
                .sorted((c1, c2) -> Double.compare(
                        ((Number) c2.get("percentualConformidade")).doubleValue(),
                        ((Number) c1.get("percentualConformidade")).doubleValue()
                ))
                .limit(10) // Top 10
                .map(comarca -> Map.of(
                        "posicao", estatisticasPorComarca.indexOf(comarca) + 1,
                        "comarca", comarca.get("comarca"),
                        "percentualConformidade", comarca.get("percentualConformidade"),
                        "emConformidade", comarca.get("emConformidade"),
                        "totalPessoas", comarca.get("totalPessoas")
                ))
                .toList();
    }

    /**
     * Obter resumo executivo em texto
     */
    public String getResumoExecutivo() {
        StringBuilder resumo = new StringBuilder();

        resumo.append("Relatório Estatístico por Comarca\n");

        if (periodoInicio != null && periodoFim != null) {
            resumo.append("Período analisado: ")
                    .append(periodoInicio.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .append(" a ")
                    .append(periodoFim.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .append(" (").append(diasPeriodo).append(" dias)\n");
        }

        if (totalComarcas != null) {
            resumo.append("Total de comarcas analisadas: ").append(totalComarcas).append("\n");
        }

        if (resumoGeral != null) {
            Object totalPessoas = resumoGeral.get("totalPessoas");
            Object totalComparecimentos = resumoGeral.get("totalComparecimentosPeriodo");

            if (totalPessoas != null) {
                resumo.append("Total de pessoas monitoradas: ").append(totalPessoas).append("\n");
            }

            if (totalComparecimentos != null) {
                resumo.append("Total de comparecimentos no período: ").append(totalComparecimentos).append("\n");
            }
        }

        if (comarcaMaiorVolume != null) {
            resumo.append("Comarca com maior volume: ").append(comarcaMaiorVolume).append("\n");
        }

        if (comarcaMaiorConformidade != null) {
            resumo.append("Comarca com maior conformidade: ").append(comarcaMaiorConformidade).append("\n");
        }

        if (mediaComparecimentosPorComarca != null) {
            resumo.append("Média de comparecimentos por comarca: ").append(mediaComparecimentosPorComarca).append("\n");
        }

        return resumo.toString();
    }

    /**
     * Verificar se o relatório contém dados
     */
    public boolean hasData() {
        return estatisticasPorComarca != null && !estatisticasPorComarca.isEmpty();
    }

    /**
     * Obter estatísticas de uma comarca específica
     */
    public Map<String, Object> getEstatisticasComarca(String nomeComarca) {
        if (estatisticasPorComarca == null || nomeComarca == null) {
            return null;
        }

        return estatisticasPorComarca.stream()
                .filter(comarca -> nomeComarca.equals(comarca.get("comarca")))
                .findFirst()
                .orElse(null);
    }

    /**
     * Calcular percentual de uma comarca em relação ao total
     */
    public Double getPercentualComarcaEmRelacaoTotal(String nomeComarca, String campo) {
        Map<String, Object> estatisticasComarca = getEstatisticasComarca(nomeComarca);
        if (estatisticasComarca == null || resumoGeral == null) {
            return null;
        }

        Object valorComarca = estatisticasComarca.get(campo);
        Object valorTotal = resumoGeral.get("total" + capitalizeFirst(campo));

        if (valorComarca == null || valorTotal == null) {
            return null;
        }

        double comarcaValue = ((Number) valorComarca).doubleValue();
        double totalValue = ((Number) valorTotal).doubleValue();

        if (totalValue == 0) {
            return 0.0;
        }

        return Math.round((comarcaValue / totalValue) * 100.0 * 100.0) / 100.0;
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // Getters e Setters
    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
        calcularEstatisticasDerivadas();
    }

    public LocalDate getPeriodoFim() {
        return periodoFim;
    }

    public void setPeriodoFim(LocalDate periodoFim) {
        this.periodoFim = periodoFim;
        calcularEstatisticasDerivadas();
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public List<Map<String, Object>> getEstatisticasPorComarca() {
        return estatisticasPorComarca;
    }

    public void setEstatisticasPorComarca(List<Map<String, Object>> estatisticasPorComarca) {
        this.estatisticasPorComarca = estatisticasPorComarca;
        calcularEstatisticasDerivadas();
    }

    public Map<String, Object> getResumoGeral() {
        return resumoGeral;
    }

    public void setResumoGeral(Map<String, Object> resumoGeral) {
        this.resumoGeral = resumoGeral;
    }

    public Map<String, Object> getMetadados() {
        return metadados;
    }

    public void setMetadados(Map<String, Object> metadados) {
        this.metadados = metadados;
    }

    public List<Map<String, Object>> getRanking() {
        return ranking;
    }

    public void setRanking(List<Map<String, Object>> ranking) {
        this.ranking = ranking;
    }

    public Integer getTotalComarcas() {
        return totalComarcas;
    }

    public Integer getDiasPeriodo() {
        return diasPeriodo;
    }

    public String getComarcaMaiorVolume() {
        return comarcaMaiorVolume;
    }

    public String getComarcaMaiorConformidade() {
        return comarcaMaiorConformidade;
    }

    public Double getMediaComparecimentosPorComarca() {
        return mediaComparecimentosPorComarca;
    }

    @Override
    public String toString() {
        return "RelatorioEstatisticoResponse{" +
                "tipoRelatorio='" + tipoRelatorio + '\'' +
                ", periodoInicio=" + periodoInicio +
                ", periodoFim=" + periodoFim +
                ", totalComarcas=" + totalComarcas +
                ", comarcaMaiorVolume='" + comarcaMaiorVolume + '\'' +
                ", comarcaMaiorConformidade='" + comarcaMaiorConformidade + '\'' +
                '}';
    }
}