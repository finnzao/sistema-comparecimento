package com.tjba.comparecimento.controller;

import com.tjba.comparecimento.dto.request.UpdateEnderecoRequest;
import com.tjba.comparecimento.dto.response.ApiResponse;
import com.tjba.comparecimento.dto.response.EnderecoResponse;
import com.tjba.comparecimento.dto.response.ViaCepResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para gerenciamento de endereços.
 */
@RestController
@RequestMapping("/enderecos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class EnderecoController {

    // TODO: Injetar EnderecoService quando implementar
    // @Autowired private EnderecoService enderecoService;

    /**
     * Buscar endereço por CEP (integração ViaCEP)
     */
    @GetMapping("/cep/{cep}")
    public ResponseEntity<ApiResponse<ViaCepResponse>> buscarPorCep(@PathVariable String cep) {
        // TODO: enderecoService.buscarPorCep(cep);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Atualizar endereço de uma pessoa
     */
    @PutMapping("/pessoa/{pessoaId}")
    public ResponseEntity<ApiResponse<EnderecoResponse>> updateEnderecoPessoa(
            @PathVariable Long pessoaId,
            @Valid @RequestBody UpdateEnderecoRequest request) {

        // TODO: enderecoService.updateEnderecoPessoa(pessoaId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Endereço atualizado com sucesso"));
    }

    /**
     * Obter endereço de uma pessoa
     */
    @GetMapping("/pessoa/{pessoaId}")
    public ResponseEntity<ApiResponse<EnderecoResponse>> getEnderecoPessoa(@PathVariable Long pessoaId) {
        // TODO: enderecoService.findByPessoaId(pessoaId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}