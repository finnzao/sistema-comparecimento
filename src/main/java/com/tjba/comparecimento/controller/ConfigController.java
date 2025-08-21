package com.tjba.comparecimento.controller;

import com.tjba.comparecimento.dto.request.ConfiguracaoSistemaRequest;
import com.tjba.comparecimento.dto.response.ApiResponse;
import com.tjba.comparecimento.dto.response.ConfiguracaoSistemaResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para configurações do sistema.
 */
@RestController
@RequestMapping("/config")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ConfigController {

    // TODO: Injetar ConfigService quando implementar
    // @Autowired private ConfigService configService;

    /**
     * Obter todas as configurações do sistema
     */
    @GetMapping("/sistema")
    public ResponseEntity<ApiResponse<List<ConfiguracaoSistemaResponse>>> getConfiguracoesSistema() {
        // TODO: configService.getAllConfiguracoes();
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Atualizar configuração específica
     */
    @PutMapping("/sistema/{chave}")
    public ResponseEntity<ApiResponse<ConfiguracaoSistemaResponse>> updateConfiguracao(
            @PathVariable String chave,
            @Valid @RequestBody ConfiguracaoSistemaRequest request) {

        // TODO: configService.updateConfiguracao(chave, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Configuração atualizada com sucesso"));
    }

    /**
     * Obter configuração por chave
     */
    @GetMapping("/sistema/{chave}")
    public ResponseEntity<ApiResponse<ConfiguracaoSistemaResponse>> getConfiguracaoPorChave(@PathVariable String chave) {
        // TODO: configService.getConfiguracaoPorChave(chave);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Resetar configurações para padrão
     */
    @PostMapping("/sistema/reset")
    public ResponseEntity<ApiResponse<String>> resetConfiguracoes() {
        // TODO: configService.resetToDefault();
        return ResponseEntity.ok(ApiResponse.success("Configurações resetadas para padrão"));
    }
}