package com.tjba.comparecimento.service;

import com.tjba.comparecimento.dto.request.ConfiguracaoSistemaRequest;
import com.tjba.comparecimento.dto.response.ConfiguracaoSistemaResponse;
import com.tjba.comparecimento.entity.ConfiguracaoSistema;
import com.tjba.comparecimento.exception.BusinessException;
import com.tjba.comparecimento.exception.ResourceNotFoundException;
import com.tjba.comparecimento.repository.ConfiguracaoSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de configurações do sistema.
 */
@Service
@Transactional
public class ConfigService {

    @Autowired
    private ConfiguracaoSistemaRepository configRepository;

    // TODO: Injetar CacheService quando implementar para cache de configurações
    // @Autowired private CacheService cacheService;

    // Configurações padrão do sistema
    private static final Map<String, String> CONFIGURACOES_PADRAO = Map.of(
            "sistema.nome", "Sistema de Comparecimento TJBA",
            "sistema.versao", "1.0.0",
            "sistema.manutencao", "false",
            "notificacao.email.ativo", "true",
            "notificacao.sms.ativo", "false",
            "comparecimento.prazo_alerta_dias", "3",
            "comparecimento.prazo_vencimento_dias", "30",
            "relatorio.max_registros", "10000",
            "backup.automatico", "true",
            "backup.horario", "02:00",
            "audit.log_ativo", "true",
            "seguranca.sessao_timeout_minutos", "60",
            "seguranca.tentativas_login_max", "5",
            "integracao.viacep.timeout", "5000",
            "integracao.viacep.retry_attempts", "3"
    );

    /**
     * Obter todas as configurações do sistema
     */
    @Transactional(readOnly = true)
    public List<ConfiguracaoSistemaResponse> getAllConfiguracoes() {
        List<ConfiguracaoSistema> configuracoes = configRepository.findAllOrderByChave();

        return configuracoes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obter configuração por chave
     */
    @Transactional(readOnly = true)
    public ConfiguracaoSistemaResponse getConfiguracaoPorChave(String chave) {
        ConfiguracaoSistema config = configRepository.findByChave(chave)
                .orElseThrow(() -> new ResourceNotFoundException("Configuração não encontrada: " + chave));

        return convertToResponse(config);
    }

    /**
     * Obter valor de configuração por chave (método utilitário)
     */
    @Transactional(readOnly = true)
    public String getValorConfiguracao(String chave) {
        // 1. Tentar buscar no cache primeiro
        // TODO: String cachedValue = cacheService.getConfig(chave);
        // if (cachedValue != null) return cachedValue;

        // 2. Buscar no banco
        return configRepository.findByChave(chave)
                .map(ConfiguracaoSistema::getValor)
                .orElse(CONFIGURACOES_PADRAO.get(chave)); // Fallback para padrão
    }

    /**
     * Obter valor de configuração como Integer
     */
    @Transactional(readOnly = true)
    public Integer getValorConfiguracaoAsInteger(String chave) {
        String valor = getValorConfiguracao(chave);
        try {
            return valor != null ? Integer.parseInt(valor) : null;
        } catch (NumberFormatException e) {
            throw new BusinessException("Configuração " + chave + " não é um número válido: " + valor);
        }
    }

    /**
     * Obter valor de configuração como Boolean
     */
    @Transactional(readOnly = true)
    public Boolean getValorConfiguracaoAsBoolean(String chave) {
        String valor = getValorConfiguracao(chave);
        return valor != null ? Boolean.parseBoolean(valor) : null;
    }

    /**
     * Atualizar configuração
     */
    public ConfiguracaoSistemaResponse updateConfiguracao(String chave, ConfiguracaoSistemaRequest request) {
        // 1. Buscar configuração existente ou criar nova
        ConfiguracaoSistema config = configRepository.findByChave(chave)
                .orElse(new ConfiguracaoSistema());

        // 2. Validar valor
        validateConfiguracao(chave, request.getValor());

        // 3. Atualizar dados
        config.setChave(chave);
        config.setValor(request.getValor());
        config.setDescricao(request.getDescricao());
        config.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);

        // 4. Salvar
        ConfiguracaoSistema savedConfig = configRepository.save(config);

        // 5. Limpar cache
        // TODO: cacheService.evictConfig(chave);

        // 6. Aplicar configuração se necessário
        aplicarConfiguracaoImediatamente(chave, request.getValor());

        // 7. Log da ação
        // TODO: auditService.logConfigUpdate(chave, request.getValor());

        return convertToResponse(savedConfig);
    }

    /**
     * Resetar configurações para valores padrão
     */
    public void resetToDefault() {
        // 1. Remover todas as configurações personalizadas
        configRepository.deleteAll();

        // 2. Recriar configurações padrão
        for (Map.Entry<String, String> entry : CONFIGURACOES_PADRAO.entrySet()) {
            ConfiguracaoSistema config = new ConfiguracaoSistema();
            config.setChave(entry.getKey());
            config.setValor(entry.getValue());
            config.setDescricao(getDescricaoPadrao(entry.getKey()));
            config.setAtivo(true);

            configRepository.save(config);
        }

        // 3. Limpar cache
        // TODO: cacheService.evictAllConfigs();

        // 4. Log da ação
        // TODO: auditService.logConfigReset();
    }

    /**
     * Inicializar configurações padrão (executado na inicialização do sistema)
     */
    @Transactional
    public void initializeDefaultConfigurations() {
        for (Map.Entry<String, String> entry : CONFIGURACOES_PADRAO.entrySet()) {
            String chave = entry.getKey();

            // Só criar se não existir
            if (!configRepository.existsByChave(chave)) {
                ConfiguracaoSistema config = new ConfiguracaoSistema();
                config.setChave(chave);
                config.setValor(entry.getValue());
                config.setDescricao(getDescricaoPadrao(chave));
                config.setAtivo(true);

                configRepository.save(config);
            }
        }
    }

    /**
     * Verificar se sistema está em manutenção
     */
    @Transactional(readOnly = true)
    public boolean isSistemaEmManutencao() {
        return getValorConfiguracaoAsBoolean("sistema.manutencao");
    }

    /**
     * Ativar/Desativar modo manutenção
     */
    public void setModoManutencao(boolean ativo) {
        ConfiguracaoSistemaRequest request = new ConfiguracaoSistemaRequest();
        request.setValor(String.valueOf(ativo));
        request.setDescricao("Modo manutenção do sistema");
        request.setAtivo(true);

        updateConfiguracao("sistema.manutencao", request);
    }

    /**
     * Obter configurações de notificação
     */
    @Transactional(readOnly = true)
    public NotificationConfig getNotificationConfig() {
        return new NotificationConfig(
                getValorConfiguracaoAsBoolean("notificacao.email.ativo"),
                getValorConfiguracaoAsBoolean("notificacao.sms.ativo"),
                getValorConfiguracaoAsInteger("comparecimento.prazo_alerta_dias"),
                getValorConfiguracaoAsInteger("comparecimento.prazo_vencimento_dias")
        );
    }

    /**
     * Obter configurações de segurança
     */
    @Transactional(readOnly = true)
    public SecurityConfig getSecurityConfig() {
        return new SecurityConfig(
                getValorConfiguracaoAsInteger("seguranca.sessao_timeout_minutos"),
                getValorConfiguracaoAsInteger("seguranca.tentativas_login_max"),
                getValorConfiguracaoAsBoolean("audit.log_ativo")
        );
    }

    // === MÉTODOS AUXILIARES ===

    private void validateConfiguracao(String chave, String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BusinessException("Valor da configuração não pode ser vazio");
        }

        // Validações específicas por tipo de configuração
        switch (chave) {
            case "comparecimento.prazo_alerta_dias":
            case "comparecimento.prazo_vencimento_dias":
            case "seguranca.sessao_timeout_minutos":
            case "seguranca.tentativas_login_max":
            case "relatorio.max_registros":
                validateIntegerValue(chave, valor);
                break;

            case "sistema.manutencao":
            case "notificacao.email.ativo":
            case "notificacao.sms.ativo":
            case "backup.automatico":
            case "audit.log_ativo":
                validateBooleanValue(chave, valor);
                break;

            case "backup.horario":
                validateTimeValue(chave, valor);
                break;

            case "integracao.viacep.timeout":
            case "integracao.viacep.retry_attempts":
                validatePositiveIntegerValue(chave, valor);
                break;
        }
    }

    private void validateIntegerValue(String chave, String valor) {
        try {
            Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new BusinessException("Configuração " + chave + " deve ser um número inteiro válido");
        }
    }

    private void validateBooleanValue(String chave, String valor) {
        if (!"true".equalsIgnoreCase(valor) && !"false".equalsIgnoreCase(valor)) {
            throw new BusinessException("Configuração " + chave + " deve ser 'true' ou 'false'");
        }
    }

    private void validateTimeValue(String chave, String valor) {
        if (!valor.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new BusinessException("Configuração " + chave + " deve ter formato HH:MM (ex: 14:30)");
        }
    }

    private void validatePositiveIntegerValue(String chave, String valor) {
        try {
            int intValue = Integer.parseInt(valor);
            if (intValue <= 0) {
                throw new BusinessException("Configuração " + chave + " deve ser um número positivo");
            }
        } catch (NumberFormatException e) {
            throw new BusinessException("Configuração " + chave + " deve ser um número inteiro positivo");
        }
    }

    private void aplicarConfiguracaoImediatamente(String chave, String valor) {
        // Aplicar configurações que precisam de ação imediata
        switch (chave) {
            case "sistema.manutencao":
                // TODO: Notificar sistema de que modo manutenção foi alterado
                break;
            case "seguranca.sessao_timeout_minutos":
                // TODO: Atualizar timeout de sessões ativas
                break;
            case "audit.log_ativo":
                // TODO: Ativar/desativar sistema de auditoria
                break;
        }
    }

    private String getDescricaoPadrao(String chave) {
        return switch (chave) {
            case "sistema.nome" -> "Nome do sistema";
            case "sistema.versao" -> "Versão atual do sistema";
            case "sistema.manutencao" -> "Indica se o sistema está em modo manutenção";
            case "notificacao.email.ativo" -> "Ativa/desativa notificações por email";
            case "notificacao.sms.ativo" -> "Ativa/desativa notificações por SMS";
            case "comparecimento.prazo_alerta_dias" -> "Dias antes do vencimento para enviar alerta";
            case "comparecimento.prazo_vencimento_dias" -> "Dias para considerar comparecimento vencido";
            case "relatorio.max_registros" -> "Máximo de registros em relatórios";
            case "backup.automatico" -> "Ativa/desativa backup automático";
            case "backup.horario" -> "Horário para execução do backup automático";
            case "audit.log_ativo" -> "Ativa/desativa log de auditoria";
            case "seguranca.sessao_timeout_minutos" -> "Timeout da sessão em minutos";
            case "seguranca.tentativas_login_max" -> "Máximo de tentativas de login";
            case "integracao.viacep.timeout" -> "Timeout para consulta ViaCEP em ms";
            case "integracao.viacep.retry_attempts" -> "Tentativas de retry para ViaCEP";
            default -> "Configuração do sistema";
        };
    }

    private ConfiguracaoSistemaResponse convertToResponse(ConfiguracaoSistema config) {
        return new ConfiguracaoSistemaResponse(
                config.getId(),
                config.getChave(),
                config.getValor(),
                config.getDescricao(),
                config.getAtivo(),
                config.getCriadoEm(),
                config.getAtualizadoEm()
        );
    }

    // === CLASSES AUXILIARES ===

    public static class NotificationConfig {
        private final Boolean emailAtivo;
        private final Boolean smsAtivo;
        private final Integer prazoAlertaDias;
        private final Integer prazoVencimentoDias;

        public NotificationConfig(Boolean emailAtivo, Boolean smsAtivo, Integer prazoAlertaDias, Integer prazoVencimentoDias) {
            this.emailAtivo = emailAtivo;
            this.smsAtivo = smsAtivo;
            this.prazoAlertaDias = prazoAlertaDias;
            this.prazoVencimentoDias = prazoVencimentoDias;
        }

        // Getters
        public Boolean getEmailAtivo() { return emailAtivo; }
        public Boolean getSmsAtivo() { return smsAtivo; }
        public Integer getPrazoAlertaDias() { return prazoAlertaDias; }
        public Integer getPrazoVencimentoDias() { return prazoVencimentoDias; }
    }

    public static class SecurityConfig {
        private final Integer sessaoTimeoutMinutos;
        private final Integer tentativasLoginMax;
        private final Boolean auditLogAtivo;

        public SecurityConfig(Integer sessaoTimeoutMinutos, Integer tentativasLoginMax, Boolean auditLogAtivo) {
            this.sessaoTimeoutMinutos = sessaoTimeoutMinutos;
            this.tentativasLoginMax = tentativasLoginMax;
            this.auditLogAtivo = auditLogAtivo;
        }

        // Getters
        public Integer getSessaoTimeoutMinutos() { return sessaoTimeoutMinutos; }
        public Integer getTentativasLoginMax() { return tentativasLoginMax; }
        public Boolean getAuditLogAtivo() { return auditLogAtivo; }
    }
}