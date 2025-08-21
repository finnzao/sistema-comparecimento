package com.tjba.comparecimento.controller;

import com.tjba.comparecimento.dto.request.CreatePessoaRequest;
import com.tjba.comparecimento.dto.request.UpdatePessoaRequest;
import com.tjba.comparecimento.dto.response.ApiResponse;
import com.tjba.comparecimento.dto.response.PessoaDetalheResponse;
import com.tjba.comparecimento.dto.response.PessoaResponse;
import com.tjba.comparecimento.entity.enums.StatusComparecimento;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller para gerenciamento de pessoas monitoradas.
 */
@RestController
@RequestMapping("/pessoas")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PessoaMonitoradaController {

    // TODO: Injetar PessoaMonitoradaService quando implementar
    // @Autowired private PessoaMonitoradaService pessoaService;

    /**
     * Listar pessoas com filtros e paginação
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PessoaResponse>>> getAllPessoas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nomeCompleto") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String rg,
            @RequestParam(required = false) StatusComparecimento status,
            @RequestParam(required = false) String comarca,
            @RequestParam(required = false) LocalDate proximoComparecimento) {

        // TODO: pessoaService.findAllWithFilters(...);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Buscar pessoa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PessoaDetalheResponse>> getPessoaById(@PathVariable Long id) {
        // TODO: pessoaService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Cadastrar nova pessoa
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PessoaResponse>> createPessoa(@Valid @RequestBody CreatePessoaRequest request) {
        // TODO: pessoaService.createPessoa(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created(null, "Pessoa cadastrada com sucesso"));
    }

    /**
     * Atualizar pessoa
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PessoaResponse>> updatePessoa(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePessoaRequest request) {

        // TODO: pessoaService.updatePessoa(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Pessoa atualizada com sucesso"));
    }

    /**
     * Buscar pessoas com comparecimento hoje
     */
    @GetMapping("/comparecimentos-hoje")
    public ResponseEntity<ApiResponse<List<PessoaResponse>>> getComparecimentosHoje() {
        // TODO: pessoaService.findComparecimentosHoje();
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Buscar pessoas em atraso
     */
    @GetMapping("/atrasados")
    public ResponseEntity<ApiResponse<List<PessoaResponse>>> getPessoasAtrasadas() {
        // TODO: pessoaService.findPessoasAtrasadas();
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Buscar por CPF
     */
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ApiResponse<PessoaResponse>> getPessoaByCpf(@PathVariable String cpf) {
        // TODO: pessoaService.findByCpf(cpf);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Buscar por número do processo
     */
    @GetMapping("/processo/{numeroProcesso}")
    public ResponseEntity<ApiResponse<PessoaResponse>> getPessoaByProcesso(@PathVariable String numeroProcesso) {
        // TODO: pessoaService.findByNumeroProcesso(numeroProcesso);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}