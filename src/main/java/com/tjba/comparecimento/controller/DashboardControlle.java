package com.tjba.comparecimento.controller;

import com.tjba.comparecimento.dto.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para dashboard e estatísticas do sistema.
 */
@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class DashboardController {

    // TODO: Injetar DashboardService quando implementar
    // @Autowired private DashboardService dashboardService;

    /**
     * Obter estatísticas gerais do dashboard
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<ApiResponse<EstatisticasGeraisResponse>> getEstatisticasGerais() {
        // TODO: dashboardService.getEstatisticasGerais();
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Obter estatísticas por comarca
     */
    @GetMapping("/estatisticas-comarca")
    public ResponseEntity<ApiResponse<List<EstatisticaComarcaResponse>>> getEstatisticasPorComarca() {
        // TODO: dashboardService.getEstatisticasPorComarca();
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Obter comparecimentos dos próximos dias
     */
    @GetMapping("/proximos-comparecimentos")
    public ResponseEntity<ApiResponse<List<ProximoComparecimentoResponse>>> getProximosComparecimentos(
            @RequestParam(defaultValue = "7") int dias) {

        // TODO: dashboardService.getProximosComparecimentos(dias);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Obter dados para gráfico de comparecimentos por mês
     */
    @GetMapping("/grafico-comparecimentos")
    public ResponseEntity<ApiResponse<GraficoComparecimentosResponse>> getGraficoComparecimentos(
            @RequestParam(defaultValue = "12") int meses) {

        // TODO: dashboardService.getGraficoComparecimentos(meses);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Obter alertas do sistema
     */
    @GetMapping("/alertas")
    public ResponseEntity<ApiResponse<List<AlertaResponse>>> getAlertas() {
        // TODO: dashboardService.getAlertas();
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Obter resumo de atividades recentes
     */
    @GetMapping("/atividades-recentes")
    public ResponseEntity<ApiResponse<List<AtividadeRecenteResponse>>> getAtividadesRecentes(
            @RequestParam(defaultValue = "10") int limite) {

        // TODO: dashboardService.getAtividadesRecentes(limite);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Obter estatísticas de desempenho
     */
    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<PerformanceResponse>> getPerformanceStats() {
        // TODO: dashboardService.getPerformanceStats();
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}