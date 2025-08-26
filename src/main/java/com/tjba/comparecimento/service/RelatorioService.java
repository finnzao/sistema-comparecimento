package com.tjba.comparecimento.service;

import com.tjba.comparecimento.dto.response.RelatorioEstatisticoResponse;
import com.tjba.comparecimento.dto.response.RelatorioInadimplentesResponse;
import com.tjba.comparecimento.entity.HistoricoComparecimento;
import com.tjba.comparecimento.entity.PessoaMonitorada;
import com.tjba.comparecimento.entity.enums.StatusComparecimento;
import com.tjba.comparecimento.entity.enums.TipoValidacao;
import com.tjba.comparecimento.exception.BusinessException;
import com.tjba.comparecimento.repository.HistoricoComparecimentoRepository;
import com.tjba.comparecimento.repository.PessoaMonitoradaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para geração de relatórios em formatos CSV, JSON e HTML.
 */
@Service
@Transactional(readOnly = true)
public class RelatorioService {

    @Autowired
    private HistoricoComparecimentoRepository historicoRepository;

    @Autowired
    private PessoaMonitoradaRepository pessoaRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Gerar relatório de comparecimentos em CSV
     */
    public Resource gerarRelatorioComparecimentosCSV(LocalDate dataInicio, LocalDate dataFim, String comarca) {
        try {
            // 1. Validar período
            validatePeriodo(dataInicio, dataFim);

            // 2. Buscar dados
            List<HistoricoComparecimento> comparecimentos = historicoRepository.findByPeriodoWithFiltersForReport(
                    dataInicio, dataFim, comarca);

            // 3. Gerar CSV
            StringBuilder csv = new StringBuilder();

            // Header
            csv.append("Data,Horário,Nome,CPF,Tipo,Validado Por,Processo,Vara,Comarca,Observações\n");

            // Dados
            for (HistoricoComparecimento comp : comparecimentos) {
                csv.append(formatCsvValue(comp.getDataComparecimento().format(DATE_FORMATTER)))
                        .append(",")
                        .append(formatCsvValue(comp.getHoraComparecimento() != null ? comp.getHoraComparecimento().toString() : ""))
                        .append(",")
                        .append(formatCsvValue(comp.getPessoaMonitorada().getNomeCompleto()))
                        .append(",")
                        .append(formatCsvValue(comp.getPessoaMonitorada().getCpf()))
                        .append(",")
                        .append(formatCsvValue(comp.getTipoValidacao().getLabel()))
                        .append(",")
                        .append(formatCsvValue(comp.getValidadoPor()))
                        .append(",")
                        .append(formatCsvValue(comp.getPessoaMonitorada().getProcessoJudicial() != null ?
                                comp.getPessoaMonitorada().getProcessoJudicial().getNumeroProcesso() : ""))
                        .append(",")
                        .append(formatCsvValue(comp.getPessoaMonitorada().getProcessoJudicial() != null ?
                                comp.getPessoaMonitorada().getProcessoJudicial().getVara() : ""))
                        .append(",")
                        .append(formatCsvValue(comp.getPessoaMonitorada().getProcessoJudicial() != null ?
                                comp.getPessoaMonitorada().getProcessoJudicial().getComarca() : ""))
                        .append(",")
                        .append(formatCsvValue(comp.getObservacoes()))
                        .append("\n");
            }

            byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);
            return new ByteArrayResource(csvBytes);

        } catch (Exception e) {
            throw new BusinessException("Erro ao gerar relatório CSV: " + e.getMessage());
        }
    }

    /**
     * Gerar relatório de pessoas em CSV
     */
    public Resource gerarRelatorioPessoasCSV(String comarca, String status) {
        try {
            // 1. Buscar dados
            List<PessoaMonitorada> pessoas = pessoaRepository.findForRelatorio(comarca, status);

            // 2. Gerar CSV
            StringBuilder csv = new StringBuilder();

            // Header
            csv.append("Nome,CPF,RG,Contato,Status,Processo,Vara,Comarca,Próximo Comparecimento,Periodicidade,Observações\n");

            // Dados
            for (PessoaMonitorada pessoa : pessoas) {
                csv.append(formatCsvValue(pessoa.getNomeCompleto()))
                        .append(",")
                        .append(formatCsvValue(pessoa.getCpf()))
                        .append(",")
                        .append(formatCsvValue(pessoa.getRg()))
                        .append(",")
                        .append(formatCsvValue(pessoa.getContato()))
                        .append(",")
                        .append(formatCsvValue(pessoa.getStatus().getLabel()))
                        .append(",")
                        .append(formatCsvValue(pessoa.getProcessoJudicial() != null ?
                                pessoa.getProcessoJudicial().getNumeroProcesso() : ""))
                        .append(",")
                        .append(formatCsvValue(pessoa.getProcessoJudicial() != null ?
                                pessoa.getProcessoJudicial().getVara() : ""))
                        .append(",")
                        .append(formatCsvValue(pessoa.getProcessoJudicial() != null ?
                                pessoa.getProcessoJudicial().getComarca() : ""))
                        .append(",")
                        .append(formatCsvValue(pessoa.getRegimeComparecimento() != null &&
                                pessoa.getRegimeComparecimento().getProximoComparecimento() != null ?
                                pessoa.getRegimeComparecimento().getProximoComparecimento().format(DATE_FORMATTER) : ""))
                        .append(",")
                        .append(formatCsvValue(pessoa.getRegimeComparecimento() != null ?
                                pessoa.getRegimeComparecimento().getPeriodicidadeDescricao() : ""))
                        .append(",")
                        .append(formatCsvValue(pessoa.getObservacoes()))
                        .append("\n");
            }

            byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);
            return new ByteArrayResource(csvBytes);

        } catch (Exception e) {
            throw new BusinessException("Erro ao gerar relatório CSV: " + e.getMessage());
        }
    }

    /**
     * Gerar relatório de inadimplentes em JSON estruturado
     */
    public RelatorioInadimplentesResponse gerarRelatorioInadimplentes() {
        try {
            // 1. Buscar pessoas inadimplentes
            List<PessoaMonitorada> inadimplentes = pessoaRepository.findByStatus(StatusComparecimento.INADIMPLENTE);
            LocalDate hoje = LocalDate.now();

            // 2. Processar dados
            var dadosInadimplentes = inadimplentes.stream()
                    .map(pessoa -> {
                        Optional<HistoricoComparecimento> ultimoComparecimento = historicoRepository.findLastComparecimentoByPessoa(pessoa.getId());

                        return Map.of(
                                "id", pessoa.getId(),
                                "nome", pessoa.getNomeCompleto(),
                                "cpf", pessoa.getCpf() != null ? pessoa.getCpf() : "",
                                "contato", pessoa.getContato() != null ? pessoa.getContato() : "",
                                "processo", pessoa.getProcessoJudicial() != null ? pessoa.getProcessoJudicial().getNumeroProcesso() : "",
                                "comarca", pessoa.getProcessoJudicial() != null ? pessoa.getProcessoJudicial().getComarca() : "",
                                "ultimoComparecimento", ultimoComparecimento.map(h -> h.getDataComparecimento().format(DATE_FORMATTER)).orElse(""),
                                "proximoComparecimento", pessoa.getRegimeComparecimento() != null && pessoa.getRegimeComparecimento().getProximoComparecimento() != null ?
                                        pessoa.getRegimeComparecimento().getProximoComparecimento().format(DATE_FORMATTER) : "",
                                "diasAtraso", pessoa.getRegimeComparecimento() != null ? pessoa.getRegimeComparecimento().getDiasAtraso() : 0,
                                "observacoes", pessoa.getObservacoes() != null ? pessoa.getObservacoes() : ""
                        );
                    })
                    .collect(Collectors.toList());

            // 3. Montar resposta estruturada
            return RelatorioInadimplentesResponse.builder()
                    .tipoRelatorio("INADIMPLENTES")
                    .dataGeracao(LocalDateTime.now())
                    .totalRegistros(inadimplentes.size())
                    .resumo(Map.of(
                            "totalInadimplentes", inadimplentes.size(),
                            "dataReferencia", hoje.format(DATE_FORMATTER)
                    ))
                    .build();

        } catch (Exception e) {
            throw new BusinessException("Erro ao gerar relatório de inadimplentes: " + e.getMessage());
        }
    }

    /**
     * Gerar relatório estatístico por comarca
     */
    public RelatorioEstatisticoResponse gerarRelatorioEstatisticasComarca(LocalDate dataInicio, LocalDate dataFim) {
        try {
            // 1. Validar período
            validatePeriodo(dataInicio, dataFim);

            // 2. Buscar comarcas
            List<String> comarcas = pessoaRepository.findDistinctComarcas();

            // 3. Calcular estatísticas por comarca
            var estatisticasPorComarca = comarcas.stream()
                    .map(comarca -> {
                        Long totalPessoas = pessoaRepository.countByComarca(comarca);
                        Long emConformidade = pessoaRepository.countByComarcaAndStatus(comarca, StatusComparecimento.EM_CONFORMIDADE);
                        Long inadimplentes = pessoaRepository.countByComarcaAndStatus(comarca, StatusComparecimento.INADIMPLENTE);
                        Long comparecimentosPeriodo = historicoRepository.countByPeriodo(dataInicio, dataFim, comarca, null);

                        Double percentualConformidade = totalPessoas > 0 ?
                                (emConformidade.doubleValue() / totalPessoas.doubleValue()) * 100.0 : 0.0;

                        return Map.<String, Object>of(
                                "comarca", comarca,
                                "totalPessoas", totalPessoas,
                                "emConformidade", emConformidade,
                                "inadimplentes", inadimplentes,
                                "percentualConformidade", Math.round(percentualConformidade * 100.0) / 100.0,
                                "comparecimentosPeriodo", comparecimentosPeriodo,
                                "taxaComparecimento", totalPessoas > 0 ?
                                        Math.round((comparecimentosPeriodo.doubleValue() / totalPessoas.doubleValue()) * 100.0) / 100.0 : 0.0
                        );
                    })
                    .collect(Collectors.toList());

            // 4. Calcular totais gerais
            Long totalGeralPessoas = pessoaRepository.count();
            Long totalGeralConformidade = pessoaRepository.countByStatus(StatusComparecimento.EM_CONFORMIDADE);
            Long totalGeralInadimplentes = pessoaRepository.countByStatus(StatusComparecimento.INADIMPLENTE);
            Long totalGeralComparecimentos = historicoRepository.countByPeriodo(dataInicio, dataFim, null, null);

            var resumoGeral = Map.<String, Object>of(
                    "totalPessoas", totalGeralPessoas,
                    "totalConformidade", totalGeralConformidade,
                    "totalInadimplentes", totalGeralInadimplentes,
                    "percentualGeralConformidade", totalGeralPessoas > 0 ?
                            Math.round((totalGeralConformidade.doubleValue() / totalGeralPessoas.doubleValue()) * 100.0 * 100.0) / 100.0 : 0.0,
                    "totalComparecimentosPeriodo", totalGeralComparecimentos,
                    "totalComarcas", comarcas.size()
            );

            // 5. Montar resposta
            return RelatorioEstatisticoResponse.builder()
                    .tipoRelatorio("ESTATISTICAS_COMARCA")
                    .periodoInicio(dataInicio)
                    .periodoFim(dataFim)
                    .estatisticasPorComarca(estatisticasPorComarca)
                    .resumoGeral(resumoGeral)
                    .build();

        } catch (Exception e) {
            throw new BusinessException("Erro ao gerar relatório estatístico: " + e.getMessage());
        }
    }

    /**
     * Gerar relatório HTML de comparecimentos
     */
    public Resource gerarRelatorioComparecimentosHTML(LocalDate dataInicio, LocalDate dataFim, String comarca) {
        try {
            // 1. Validar período
            validatePeriodo(dataInicio, dataFim);

            // 2. Buscar dados
            List<HistoricoComparecimento> comparecimentos = historicoRepository.findByPeriodoWithFiltersForReport(
                    dataInicio, dataFim, comarca);

            // 3. Gerar HTML
            StringBuilder html = new StringBuilder();

            html.append("""
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Relatório de Comparecimentos</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        .header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #333; padding-bottom: 10px; }
                        .info { margin: 20px 0; }
                        .info strong { color: #2563eb; }
                        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                        th { background-color: #2563eb; color: white; font-weight: bold; }
                        tr:nth-child(even) { background-color: #f9f9f9; }
                        .footer { margin-top: 30px; text-align: center; font-size: 12px; color: #666; }
                        .summary { background-color: #e3f2fd; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>RELATÓRIO DE COMPARECIMENTOS</h1>
                        <h3>Sistema de Controle de Comparecimento - TJBA</h3>
                    </div>
                """);

            html.append("<div class=\"info\">")
                    .append("<p><strong>Período:</strong> ")
                    .append(dataInicio.format(DATE_FORMATTER))
                    .append(" a ")
                    .append(dataFim.format(DATE_FORMATTER))
                    .append("</p>");

            if (comarca != null) {
                html.append("<p><strong>Comarca:</strong> ").append(comarca).append("</p>");
            }

            html.append("<p><strong>Data de Geração:</strong> ")
                    .append(LocalDateTime.now().format(DATETIME_FORMATTER))
                    .append("</p>")
                    .append("<p><strong>Total de Registros:</strong> ")
                    .append(comparecimentos.size())
                    .append("</p>")
                    .append("</div>");

            // Resumo por tipo
            Map<TipoValidacao, Long> resumoPorTipo = comparecimentos.stream()
                    .collect(Collectors.groupingBy(
                            HistoricoComparecimento::getTipoValidacao,
                            Collectors.counting()
                    ));

            html.append("<div class=\"summary\">")
                    .append("<h3>Resumo por Tipo de Validação</h3>");

            for (Map.Entry<TipoValidacao, Long> entry : resumoPorTipo.entrySet()) {
                html.append("<p><strong>")
                        .append(entry.getKey().getLabel())
                        .append(":</strong> ")
                        .append(entry.getValue())
                        .append(" registro")
                        .append(entry.getValue() > 1 ? "s" : "")
                        .append("</p>");
            }
            html.append("</div>");

            // Tabela de dados
            html.append("""
                <table>
                    <thead>
                        <tr>
                            <th>Data</th>
                            <th>Horário</th>
                            <th>Nome</th>
                            <th>CPF</th>
                            <th>Tipo</th>
                            <th>Processo</th>
                            <th>Comarca</th>
                            <th>Validado Por</th>
                        </tr>
                    </thead>
                    <tbody>
                """);

            for (HistoricoComparecimento comp : comparecimentos) {
                html.append("<tr>")
                        .append("<td>").append(comp.getDataComparecimento().format(DATE_FORMATTER)).append("</td>")
                        .append("<td>").append(comp.getHoraComparecimento() != null ? comp.getHoraComparecimento().toString() : "").append("</td>")
                        .append("<td>").append(escapeHtml(comp.getPessoaMonitorada().getNomeCompleto())).append("</td>")
                        .append("<td>").append(comp.getPessoaMonitorada().getCpf() != null ? comp.getPessoaMonitorada().getCpf() : "").append("</td>")
                        .append("<td>").append(comp.getTipoValidacao().getLabel()).append("</td>")
                        .append("<td>").append(comp.getPessoaMonitorada().getProcessoJudicial() != null ?
                                comp.getPessoaMonitorada().getProcessoJudicial().getNumeroProcesso() : "").append("</td>")
                        .append("<td>").append(comp.getPessoaMonitorada().getProcessoJudicial() != null ?
                                comp.getPessoaMonitorada().getProcessoJudicial().getComarca() : "").append("</td>")
                        .append("<td>").append(escapeHtml(comp.getValidadoPor())).append("</td>")
                        .append("</tr>");
            }

            html.append("""
                    </tbody>
                </table>
                
                <div class="footer">
                    <p>Relatório gerado automaticamente pelo Sistema de Controle de Comparecimento</p>
                    <p>Tribunal de Justiça da Bahia - TJBA</p>
                </div>
                
                </body>
                </html>
                """);

            byte[] htmlBytes = html.toString().getBytes(StandardCharsets.UTF_8);
            return new ByteArrayResource(htmlBytes);

        } catch (Exception e) {
            throw new BusinessException("Erro ao gerar relatório HTML: " + e.getMessage());
        }
    }

    /**
     * Gerar dados estruturados para relatório personalizado
     */
    public Map<String, Object> gerarDadosRelatorioPersonalizado(
            LocalDate dataInicio,
            LocalDate dataFim,
            String comarca,
            List<String> tiposValidacao,
            boolean incluirEstatisticas) {

        try {
            validatePeriodo(dataInicio, dataFim);

            // Buscar comparecimentos
            List<HistoricoComparecimento> comparecimentos = historicoRepository.findByPeriodoWithFiltersForReport(
                    dataInicio, dataFim, comarca);

            // Filtrar por tipos de validação se especificado
            if (tiposValidacao != null && !tiposValidacao.isEmpty()) {
                List<TipoValidacao> tipos = tiposValidacao.stream()
                        .map(TipoValidacao::valueOf)
                        .collect(Collectors.toList());

                comparecimentos = comparecimentos.stream()
                        .filter(comp -> tipos.contains(comp.getTipoValidacao()))
                        .collect(Collectors.toList());
            }

            // Dados básicos
            var dadosRelatorio = Map.<String, Object>of(
                    "metadados", Map.of(
                            "dataInicio", dataInicio.format(DATE_FORMATTER),
                            "dataFim", dataFim.format(DATE_FORMATTER),
                            "comarca", comarca != null ? comarca : "TODAS",
                            "totalRegistros", comparecimentos.size(),
                            "dataGeracao", LocalDateTime.now().format(DATETIME_FORMATTER)
                    ),
                    "comparecimentos", comparecimentos.stream()
                            .map(this::mapearComparecimentoParaRelatorio)
                            .collect(Collectors.toList())
            );

            // Adicionar estatísticas se solicitado
            if (incluirEstatisticas) {
                var estatisticas = calcularEstatisticasComparecimentos(comparecimentos);
                return Map.of(
                        "dados", dadosRelatorio,
                        "estatisticas", estatisticas
                );
            }

            return Map.of("dados", dadosRelatorio);

        } catch (Exception e) {
            throw new BusinessException("Erro ao gerar dados do relatório: " + e.getMessage());
        }
    }

    // === MÉTODOS AUXILIARES ===

    private void validatePeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }

        if (dataInicio.isBefore(LocalDate.now().minusYears(5))) {
            throw new BusinessException("Data de início não pode ser anterior a 5 anos");
        }
    }

    private String formatCsvValue(String value) {
        if (value == null) {
            return "";
        }

        // Escapar aspas e adicionar aspas se contém vírgula, quebra de linha ou aspas
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    private Map<String, Object> mapearComparecimentoParaRelatorio(HistoricoComparecimento comp) {
        return Map.of(
                "id", comp.getId(),
                "data", comp.getDataComparecimento().format(DATE_FORMATTER),
                "horario", comp.getHoraComparecimento() != null ? comp.getHoraComparecimento().toString() : "",
                "pessoa", Map.of(
                        "id", comp.getPessoaMonitorada().getId(),
                        "nome", comp.getPessoaMonitorada().getNomeCompleto(),
                        "cpf", comp.getPessoaMonitorada().getCpf() != null ? comp.getPessoaMonitorada().getCpf() : ""
                ),
                "processo", comp.getPessoaMonitorada().getProcessoJudicial() != null ? Map.of(
                        "numero", comp.getPessoaMonitorada().getProcessoJudicial().getNumeroProcesso(),
                        "vara", comp.getPessoaMonitorada().getProcessoJudicial().getVara(),
                        "comarca", comp.getPessoaMonitorada().getProcessoJudicial().getComarca()
                ) : Map.of(),
                "validacao", Map.of(
                        "tipo", comp.getTipoValidacao().name(),
                        "tipoLabel", comp.getTipoValidacao().getLabel(),
                        "validadoPor", comp.getValidadoPor()
                ),
                "observacoes", comp.getObservacoes() != null ? comp.getObservacoes() : ""
        );
    }

    private Map<String, Object> calcularEstatisticasComparecimentos(List<HistoricoComparecimento> comparecimentos) {
        // Estatísticas por tipo
        Map<String, Long> porTipo = comparecimentos.stream()
                .collect(Collectors.groupingBy(
                        comp -> comp.getTipoValidacao().getLabel(),
                        Collectors.counting()
                ));

        // Estatísticas por comarca
        Map<String, Long> porComarca = comparecimentos.stream()
                .collect(Collectors.groupingBy(
                        comp -> comp.getPessoaMonitorada().getProcessoJudicial() != null ?
                                comp.getPessoaMonitorada().getProcessoJudicial().getComarca() : "SEM COMARCA",
                        Collectors.counting()
                ));

        // Estatísticas por validador
        Map<String, Long> porValidador = comparecimentos.stream()
                .collect(Collectors.groupingBy(
                        HistoricoComparecimento::getValidadoPor,
                        Collectors.counting()
                ));

        return Map.of(
                "porTipo", porTipo,
                "porComarca", porComarca,
                "porValidador", porValidador,
                "totalComparecimentos", comparecimentos.size()
        );
    }
}