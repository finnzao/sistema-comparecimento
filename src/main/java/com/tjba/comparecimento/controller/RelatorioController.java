package com.tjba.comparecimento.controller;

import com.tjba.comparecimento.dto.response.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller para geração e download de relatórios.
 */
@RestController
@RequestMapping("/relatorios")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class RelatorioController {

    // TODO: Injetar RelatorioService quando implementar
    // @Autowired private RelatorioService relatorioService;

    /**
     * Gerar relatório de comparecimentos em Excel
     */
    @GetMapping("/comparecimentos/excel")
    public ResponseEntity<Resource> gerarRelatorioComparecimentosExcel(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam(required = false) String comarca) {

        // TODO: relatorioService.gerarRelatorioComparecimentosExcel(dataInicio, dataFim, comarca);
        return ResponseEntity.ok().build();
    }

    /**
     * Gerar relatório de pessoas em PDF
     */
    @GetMapping("/pessoas/pdf")
    public ResponseEntity<Resource> gerarRelatorioPessoasPdf(
            @RequestParam(required = false) String comarca,
            @RequestParam(required = false) String status) {

        // TODO: relatorioService.gerarRelatorioPessoasPdf(comarca, status);
        return ResponseEntity.ok().build();
    }

    /**
     * Gerar relatório de inadimplentes
     */
    @GetMapping("/inadimplentes/excel")
    public ResponseEntity<Resource> gerarRelatorioInadimplentesExcel() {
        // TODO: relatorioService.gerarRelatorioInadimplentesExcel();
        return ResponseEntity.ok().build();
    }

    /**
     * Gerar relatório estatístico por comarca
     */
    @GetMapping("/estatisticas-comarca/pdf")
    public ResponseEntity<Resource> gerarRelatorioEstatisticasComarcaPdf(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {

        // TODO: relatorioService.gerarRelatorioEstatisticasComarcaPdf(dataInicio, dataFim);
        return ResponseEntity.ok().build();
    }
}