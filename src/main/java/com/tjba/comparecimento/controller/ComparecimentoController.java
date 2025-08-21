package com.tjba.comparecimento.controller;

import com.tjba.comparecimento.dto.request.AtualizarProximoComparecimentoRequest;
import com.tjba.comparecimento.dto.request.RegistrarComparecimentoPresencialRequest;
import com.tjba.comparecimento.dto.request.RegistrarComparecimentoVirtualRequest;
import com.tjba.comparecimento.dto.request.RegistrarJustificativaRequest;
import com.tjba.comparecimento.dto.response.ApiResponse;
import com.tjba.comparecimento.dto.response.ComparecimentoResponse;
import com.tjba.comparecimento.dto.response.HistoricoComparecimentoResponse;
import com.tjba.comparecimento.dto.response.RelatorioComparecimentoResponse;
import com.tjba.comparecimento.entity.enums.TipoValidacao;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller para registro e controle de comparecimentos.
 */
@RestController
@RequestMapping("/comparecimentos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ComparecimentoController {

    // TODO: Injetar ComparecimentoService quando implementar
    // @Autowired private ComparecimentoService comparecimentoService;

    /**
     * Registrar comparecimento presencial
     */
    @PostMapping("/presencial")
    public ResponseEntity<ApiResponse<ComparecimentoResponse>> registrarComparecimentoPresencial(
            @Valid @RequestBody RegistrarComparecimentoPresencialRequest request) {

        // TODO: comparecimentoService.registrarPresencial(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created(null, "Comparecimento presencial registrado com sucesso"));
    }

    /**
     * Registrar comparecimento virtual
     */
    @PostMapping("/virtual")
    public ResponseEntity<ApiResponse<ComparecimentoResponse>> registrarComparecimentoVirtual(
            @Valid @RequestBody RegistrarComparecimentoVirtualRequest request) {

        // TODO: comparecimentoService.registrarVirtual(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created(null, "Comparecimento virtual registrado com sucesso"));
    }

    /**
     * Registrar justificativa de ausência
     */
    @PostMapping("/justificativa")
    public ResponseEntity<ApiResponse<ComparecimentoResponse>> registrarJustificativa(
            @Valid @RequestBody RegistrarJustificativaRequest request) {

        // TODO: comparecimentoService.registrarJustificativa(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created(null, "Justificativa registrada com sucesso"));
    }

    /**
     * Listar histórico de comparecimentos de uma pessoa
     */
    @GetMapping("/pessoa/{pessoaId}")
    public ResponseEntity<ApiResponse<Page<HistoricoComparecimentoResponse>>> getHistoricoComparecimentos(
            @PathVariable Long pessoaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataComparecimento") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        // TODO: comparecimentoService.findHistoricoByPessoa(pessoaId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Atualizar próximo comparecimento
     */
    @PatchMapping("/pessoa/{pessoaId}/proximo-comparecimento")
    public ResponseEntity<ApiResponse<String>> atualizarProximoComparecimento(
            @PathVariable Long pessoaId,
            @Valid @RequestBody AtualizarProximoComparecimentoRequest request) {

        // TODO: comparecimentoService.atualizarProximoComparecimento(pessoaId, request);
        return ResponseEntity.ok(ApiResponse.success("Próximo comparecimento atualizado com sucesso"));
    }

    /**
     * Gerar relatório de comparecimentos por período
     */
    @GetMapping("/relatorio")
    public ResponseEntity<ApiResponse<RelatorioComparecimentoResponse>> gerarRelatorio(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam(required = false) String comarca,
            @RequestParam(required = false) TipoValidacao tipoValidacao) {

        // TODO: comparecimentoService.gerarRelatorio(dataInicio, dataFim, comarca, tipoValidacao);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Buscar comparecimentos por período
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<HistoricoComparecimentoResponse>>> getComparecimentosPorPeriodo(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) TipoValidacao tipoValidacao,
            @RequestParam(required = false) String comarca,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // TODO: comparecimentoService.findByPeriodo(...);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}