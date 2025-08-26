package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DTO para resposta de relatório de inadimplentes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelatorioInadimplentesResponse {

    private String tipoRelatorio;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataGeracao;

    private Integer totalRegistros;
    private List<Map<String, Object>> dados;
    private Map<String, Object> resumo;
    private Map<String, Object> metadados;

    // Estatísticas calculadas
    private Integer totalInadimplentes;
    private Long mediaDiasAtraso;
    private Long maxDiasAtraso;
    private String comarcaComMaisInadimplentes;
    private Map<String, Long> distribuicaoPorComarca;

    // Constructors
    public RelatorioInadimplentesResponse() {
        this.dataGeracao = LocalDateTime.now();
        this.tipoRelatorio = "INADIMPLENTES";
    }

    public RelatorioInadimplentesResponse(List<Map<String, Object>> dados, Map<String, Object> resumo) {
        this();
        this.dados = dados;
        this.resumo = resumo;
        this.totalRegistros = dados != null ? dados.size() : 0;
        calcularEstatisticasDerivadas();
    }

    /**
     * Builder pattern
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RelatorioInadimplentesResponse response = new RelatorioInadimplentesResponse();

        public Builder tipoRelatorio(String tipoRelatorio) {
            response.tipoRelatorio = tipoRelatorio;
            return this;
        }

        public Builder dataGeracao(LocalDateTime dataGeracao) {
            response.dataGeracao = dataGeracao;
            return this;
        }

        public Builder totalRegistros(Integer totalRegistros) {
            response.totalRegistros = totalRegistros;
            return this;
        }

        public Builder dados(List<Map<String, Object>> dados) {
            response.dados = dados;
            response.totalRegistros = dados != null ? dados.size() : 0;
            return this;
        }

        public Builder resumo(Map<String, Object> resumo) {
            response.resumo = resumo;
            return this;
        }

        public Builder metadados(Map<String, Object> metadados) {
            response.metadados = metadados;
            return this;
        }

        public RelatorioInadimplentesResponse build() {
            response.calcularEstatisticasDerivadas();
            return response;
        }
    }

    /**
     * Calcular estatísticas derivadas dos dados
     */
    private void calcularEstatisticasDerivadas() {
        if (dados == null || dados.isEmpty()) {
            return;
        }

        this.totalInadimplentes = dados.size();

        // Calcular média e máximo de dias em atraso
        var diasAtraso = dados.stream()
                .mapToLong(pessoa -> {
                    Object dias = pessoa.get("diasAtraso");
                    return dias instanceof Number ? ((Number) dias).longValue() : 0L;
                })
                .filter(dias -> dias > 0);

        this.mediaDiasAtraso = (long) diasAtraso.average().orElse(0.0);
        this.maxDiasAtraso = diasAtraso.max().orElse(0L);

        // Distribuição por comarca
        this.distribuicaoPorComarca = dados.stream()
                .collect(Collectors.groupingBy(
                        pessoa -> (String) pessoa.getOrDefault("comarca", "SEM COMARCA"),
                        Collectors.counting()
                ));

        // Comarca com mais inadimplentes
        this.comarcaComMaisInadimplentes = distribuicaoPorComarca.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Obter ranking de pessoas por dias em atraso
     */
    public List<Map<String, Object>> getRankingPorAtraso() {
        if (dados == null) {
            return List.of();
        }

        return dados.stream()
                .sorted((p1, p2) -> Long.compare(
                        ((Number) p2.getOrDefault("diasAtraso", 0)).longValue(),
                        ((Number) p1.getOrDefault("diasAtraso", 0)).longValue()
                ))
                .limit(10) // Top 10
                .map(pessoa -> Map.of(
                        "nome", pessoa.get("nome"),
                        "comarca", pessoa.getOrDefault("comarca", ""),
                        "diasAtraso", pessoa.getOrDefault("diasAtraso", 0),
                        "contato", pessoa.getOrDefault("contato", "")
                ))
                .collect(Collectors.toList());
    }

    /**
     * Obter inadimplentes por faixa de atraso
     */
    public Map<String, Long> getDistribuicaoPorFaixaAtraso() {
        if (dados == null) {
            return Map.of();
        }

        return dados.stream()
                .collect(Collectors.groupingBy(
                        pessoa -> {
                            long dias = ((Number) pessoa.getOrDefault("diasAtraso", 0)).longValue();
                            if (dias <= 30) return "Até 30 dias";
                            if (dias <= 60) return "31 a 60 dias";
                            if (dias <= 90) return "61 a 90 dias";
                            if (dias <= 180) return "91 a 180 dias";
                            return "Mais de 180 dias";
                        },
                        Collectors.counting()
                ));
    }

    /**
     * Verificar se há inadimplentes críticos (mais de 90 dias)
     */
    public boolean hasInadimplentiesCriticos() {
        if (dados == null) {
            return false;
        }

        return dados.stream()
                .anyMatch(pessoa -> ((Number) pessoa.getOrDefault("diasAtraso", 0)).longValue() > 90);
    }

    /**
     * Contar inadimplentes críticos
     */
    public long countInadimplentiesCriticos() {
        if (dados == null) {
            return 0;
        }

        return dados.stream()
                .mapToLong(pessoa -> ((Number) pessoa.getOrDefault("diasAtraso", 0)).longValue())
                .filter(dias -> dias > 90)
                .count();
    }

    /**
     * Obter resumo textual
     */
    public String getResumoTextual() {
        StringBuilder resumo = new StringBuilder();

        resumo.append("Relatório de Pessoas Inadimplentes\n");

        if (totalInadimplentes != null) {
            resumo.append("Total de inadimplentes: ").append(totalInadimplentes).append("\n");
        }

        if (mediaDiasAtraso != null) {
            resumo.append("Média de dias em atraso: ").append(mediaDiasAtraso).append("\n");
        }

        if (maxDiasAtraso != null) {
            resumo.append("Maior atraso registrado: ").append(maxDiasAtraso).append(" dias\n");
        }

        if (comarcaComMaisInadimplentes != null) {
            resumo.append("Comarca com mais inadimplentes: ").append(comarcaComMaisInadimplentes).append("\n");
        }

        long criticos = countInadimplentiesCriticos();
        if (criticos > 0) {
            resumo.append("Inadimplentes críticos (>90 dias): ").append(criticos).append("\n");
        }

        return resumo.toString();
    }

    /**
     * Verificar se o relatório contém dados
     */
    public boolean hasData() {
        return dados != null && !dados.isEmpty();
    }

    // Getters e Setters
    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public List<Map<String, Object>> getDados() {
        return dados;
    }

    public void setDados(List<Map<String, Object>> dados) {
        this.dados = dados;
        this.totalRegistros = dados != null ? dados.size() : 0;
        calcularEstatisticasDerivadas();
    }

    public Map<String, Object> getResumo() {
        return resumo;
    }

    public void setResumo(Map<String, Object> resumo) {
        this.resumo = resumo;
    }

    public Map<String, Object> getMetadados() {
        return metadados;
    }

    public void setMetadados(Map<String, Object> metadados) {
        this.metadados = metadados;
    }

    public Integer getTotalInadimplentes() {
        return totalInadimplentes;
    }

    public Long getMediaDiasAtraso() {
        return mediaDiasAtraso;
    }

    public Long getMaxDiasAtraso() {
        return maxDiasAtraso;
    }

    public String getComarcaComMaisInadimplentes() {
        return comarcaComMaisInadimplentes;
    }

    public Map<String, Long> getDistribuicaoPorComarca() {
        return distribuicaoPorComarca;
    }

    @Override
    public String toString() {
        return "RelatorioInadimplentesResponse{" +
                "tipoRelatorio='" + tipoRelatorio + '\'' +
                ", totalInadimplentes=" + totalInadimplentes +
                ", mediaDiasAtraso=" + mediaDiasAtraso +
                ", comarcaComMaisInadimplentes='" + comarcaComMaisInadimplentes + '\'' +
                '}';
    }
}