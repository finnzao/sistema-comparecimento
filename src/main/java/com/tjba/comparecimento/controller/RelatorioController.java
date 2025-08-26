package com.tjba.comparecimento.controller;

import com.tjba.comparecimento.dto.response.ApiResponse;
import com.tjba.comparecimento.dto.response.RelatorioComparecimentoResponse;
import com.tjba.comparecimento.dto.response.RelatorioEstatisticoResponse;
import com.tjba.comparecimento.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Controller para geração e download de relatórios em diversos formatos.
 */
@RestController
@RequestMapping("/relatorios")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    private static final DateTimeFormatter FILENAME_DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

    /**
     * Gerar relatório de comparecimentos em CSV
     */
    @GetMapping("/comparecimentos/csv")
    public ResponseEntity<Resource> gerarRelatorioComparecimentosCSV(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam(required = false) String comarca) {

        Resource resource = relatorioService.gerarRelatorioComparecimentosCSV(dataInicio, dataFim, comarca);

        String filename = String.format("comparecimentos_%s_%s%s.csv",
                dataInicio.format(FILENAME_DATE_FORMAT),
                dataFim.format(FILENAME_DATE_FORMAT),
                comarca != null ? "_" + comarca.toLowerCase().replaceAll("[^a-zA-Z0-9]", "") : "");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                .body(resource);
    }

    /**
     * Gerar relatório de pessoas em CSV
     */
    @GetMapping("/pessoas/csv")
    public ResponseEntity<Resource> gerarRelatorioPessoasCSV(
            @RequestParam(required = false) String comarca,
            @RequestParam(required = false) String status) {

        Resource resource = relatorioService.gerarRelatorioPessoasCSV(comarca, status);

        String filename = String.format("pessoas_%s%s%s.csv",
                LocalDate.now().format(FILENAME_DATE_FORMAT),
                comarca != null ? "_" + comarca.toLowerCase().replaceAll("[^a-zA-Z0-9]", "") : "",
                status != null ? "_" + status.toLowerCase().replaceAll("[^a-zA-Z0-9]", "") : "");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                .body(resource);
    }

    /**
     * Gerar relatório de comparecimentos em HTML
     */
    @GetMapping("/comparecimentos/html")
    public ResponseEntity<Resource> gerarRelatorioComparecimentosHTML(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam(required = false) String comarca) {

        Resource resource = relatorioService.gerarRelatorioComparecimentosHTML(dataInicio, dataFim, comarca);

        String filename = String.format("relatorio_comparecimentos_%s_%s%s.html",
                dataInicio.format(FILENAME_DATE_FORMAT),
                dataFim.format(FILENAME_DATE_FORMAT),
                comarca != null ? "_" + comarca.toLowerCase().replaceAll("[^a-zA-Z0-9]", "") : "");

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                .body(resource);
    }

    /**
     * Gerar relatório de inadimplentes (JSON estruturado)
     */
    @GetMapping("/inadimplentes")
    public ResponseEntity<ApiResponse<RelatorioComparecimentoResponse>> gerarRelatorioInadimplentes() {
        RelatorioComparecimentoResponse relatorio = relatorioService.gerarRelatorioInadimplentes();
        return ResponseEntity.ok(ApiResponse.success(relatorio, "Relatório de inadimplentes gerado com sucesso"));
    }

    /**
     * Gerar relatório estatístico por comarca (JSON estruturado)
     */
    @GetMapping("/estatisticas-comarca")
    public ResponseEntity<ApiResponse<RelatorioEstatisticoResponse>> gerarRelatorioEstatisticasComarca(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {

        RelatorioEstatisticoResponse relatorio = relatorioService.gerarRelatorioEstatisticasComarca(dataInicio, dataFim);
        return ResponseEntity.ok(ApiResponse.success(relatorio, "Relatório estatístico gerado com sucesso"));
    }

    /**
     * Gerar dados para relatório personalizado (JSON)
     */
    @GetMapping("/personalizado")
    public ResponseEntity<ApiResponse<Map<String, Object>>> gerarDadosRelatorioPersonalizado(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam(required = false) String comarca,
            @RequestParam(required = false) List<String> tiposValidacao,
            @RequestParam(defaultValue = "false") boolean incluirEstatisticas) {

        Map<String, Object> dados = relatorioService.gerarDadosRelatorioPersonalizado(
                dataInicio, dataFim, comarca, tiposValidacao, incluirEstatisticas);

        return ResponseEntity.ok(ApiResponse.success(dados, "Dados do relatório gerados com sucesso"));
    }

    /**
     * Endpoint para download direto de relatório CSV de comparecimentos (método simplificado)
     */
    @GetMapping("/download/comparecimentos")
    public ResponseEntity<Resource> downloadRelatorioComparecimentos(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam(required = false) String comarca,
            @RequestParam(defaultValue = "csv") String formato) {

        Resource resource;
        MediaType contentType;
        String extension;

        switch (formato.toLowerCase()) {
            case "html":
                resource = relatorioService.gerarRelatorioComparecimentosHTML(dataInicio, dataFim, comarca);
                contentType = MediaType.TEXT_HTML;
                extension = "html";
                break;
            case "csv":
            default:
                resource = relatorioService.gerarRelatorioComparecimentosCSV(dataInicio, dataFim, comarca);
                contentType = MediaType.parseMediaType("text/csv");
                extension = "csv";
                break;
        }

        String filename = String.format("relatorio_comparecimentos_%s_%s%s.%s",
                dataInicio.format(FILENAME_DATE_FORMAT),
                dataFim.format(FILENAME_DATE_FORMAT),
                comarca != null ? "_" + comarca.toLowerCase().replaceAll("[^a-zA-Z0-9]", "") : "",
                extension);

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                .body(resource);
    }

    /**
     * Endpoint para download direto de relatório de pessoas
     */
    @GetMapping("/download/pessoas")
    public ResponseEntity<Resource> downloadRelatorioPessoas(
            @RequestParam(required = false) String comarca,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "csv") String formato) {

        // Por enquanto, apenas CSV está disponível para pessoas
        Resource resource = relatorioService.gerarRelatorioPessoasCSV(comarca, status);

        String filename = String.format("relatorio_pessoas_%s%s%s.csv",
                LocalDate.now().format(FILENAME_DATE_FORMAT),
                comarca != null ? "_" + comarca.toLowerCase().replaceAll("[^a-zA-Z0-9]", "") : "",
                status != null ? "_" + status.toLowerCase().replaceAll("[^a-zA-Z0-9]", "") : "");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                .body(resource);
    }

    /**
     * Obter metadados sobre os relatórios disponíveis
     */
    @GetMapping("/metadata")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRelatoriosMetadata() {
        Map<String, Object> metadata = Map.of(
                "formatosSuportados", Map.of(
                        "comparecimentos", List.of("csv", "html", "json"),
                        "pessoas", List.of("csv"),
                        "inadimplentes", List.of("json"),
                        "estatisticas", List.of("json"),
                        "personalizado", List.of("json")
                ),
                "parametrosObrigatorios", Map.of(
                        "comparecimentos", List.of("dataInicio", "dataFim"),
                        "pessoas", List.of(),
                        "inadimplentes", List.of(),
                        "estatisticas", List.of("dataInicio", "dataFim"),
                        "personalizado", List.of("dataInicio", "dataFim")
                ),
                "parametrosOpcionais", Map.of(
                        "comparecimentos", List.of("comarca"),
                        "pessoas", List.of("comarca", "status"),
                        "inadimplentes", List.of(),
                        "estatisticas", List.of(),
                        "personalizado", List.of("comarca", "tiposValidacao", "incluirEstatisticas")
                ),
                "limitesMaximos", Map.of(
                        "periodoMaximo", "5 anos",
                        "registrosMaximos", "sem limite"
                ),
                "exemplos", Map.of(
                        "urlComparecimentosCSV", "/relatorios/comparecimentos/csv?dataInicio=2024-01-01&dataFim=2024-01-31",
                        "urlPessoasCSV", "/relatorios/pessoas/csv?comarca=Salvador&status=inadimplente",
                        "urlInadimplentes", "/relatorios/inadimplentes",
                        "urlEstatisticas", "/relatorios/estatisticas-comarca?dataInicio=2024-01-01&dataFim=2024-12-31"
                )
        );

        return ResponseEntity.ok(ApiResponse.success(metadata, "Metadados dos relatórios obtidos com sucesso"));
    }

    /**
     * Endpoint de saúde para verificar se o serviço de relatórios está funcionando
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        Map<String, String> health = Map.of(
                "status", "OK",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "service", "RelatorioService",
                "version", "1.0.0"
        );

        return ResponseEntity.ok(ApiResponse.success(health, "Serviço de relatórios funcionando corretamente"));
    }

    /**
     * Obter estatísticas rápidas de uso dos relatórios (simulado)
     */
    @GetMapping("/estatisticas-uso")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEstatisticasUso() {
        // Esta implementação seria conectada a um sistema de métricas real
        Map<String, Object> estatisticas = Map.of(
                "totalRelatoriosGerados", 1247,
                "formatoMaisUsado", "CSV",
                "relatorioMaisGerado", "comparecimentos",
                "ultimaGeracao", LocalDate.now().minusDays(1).toString(),
                "mediaGeracoesPorDia", 15.3,
                "distribucaoPorFormato", Map.of(
                        "CSV", 65,
                        "HTML", 25,
                        "JSON", 10
                )
        );

        return ResponseEntity.ok(ApiResponse.success(estatisticas, "Estatísticas de uso obtidas com sucesso"));
    }
}