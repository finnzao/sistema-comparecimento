package com.tjba.comparecimento.service;

import com.tjba.comparecimento.dto.request.*;
import com.tjba.comparecimento.dto.response.*;
import com.tjba.comparecimento.entity.*;
import com.tjba.comparecimento.entity.enums.StatusComparecimento;
import com.tjba.comparecimento.entity.enums.TipoValidacao;
import com.tjba.comparecimento.exception.BusinessException;
import com.tjba.comparecimento.exception.ResourceNotFoundException;
import com.tjba.comparecimento.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Service para registro e controle de comparecimentos.
 */
@Service
@Transactional
public class ComparecimentoService {

    @Autowired
    private HistoricoComparecimentoRepository historicoRepository;

    @Autowired
    private PessoaMonitoradaRepository pessoaRepository;

    @Autowired
    private RegimeComparecimentoRepository regimeRepository;

    // TODO: Injetar NotificationService quando implementar
    // @Autowired private NotificationService notificationService;

    // TODO: Injetar EmailService quando implementar
    // @Autowired private EmailService emailService;

    /**
     * Registrar comparecimento presencial
     */
    public ComparecimentoResponse registrarPresencial(RegistrarComparecimentoPresencialRequest request) {
        // 1. Buscar pessoa
        PessoaMonitorada pessoa = pessoaRepository.findById(request.getPessoaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + request.getPessoaId()));

        // 2. Validar se não há comparecimento duplicado no dia
        validateComparecimentoDuplicado(request.getPessoaId(), LocalDate.now());

        // 3. Criar histórico de comparecimento
        HistoricoComparecimento historico = new HistoricoComparecimento();
        historico.setPessoaMonitorada(pessoa);
        historico.setDataComparecimento(LocalDate.now());
        historico.setHoraComparecimento(LocalTime.now());
        historico.setTipoValidacao(TipoValidacao.PRESENCIAL);
        historico.setValidadoPor(request.getValidadoPor());
        historico.setObservacoes(request.getObservacoes());

        HistoricoComparecimento savedHistorico = historicoRepository.save(historico);

        // 4. Atualizar regime de comparecimento
        LocalDate proximoComparecimento = calcularProximoComparecimento(pessoa);
        atualizarRegimeComparecimento(pessoa, proximoComparecimento);

        // 5. Atualizar status da pessoa para em conformidade
        pessoa.setStatus(StatusComparecimento.EM_CONFORMIDADE);
        pessoaRepository.save(pessoa);

        // 6. Enviar notificação de comparecimento registrado
        // TODO: notificationService.sendComparecimentoRegistradoNotification(pessoa, savedHistorico);

        // 7. Log da ação
        // TODO: auditService.logComparecimentoRegistrado(savedHistorico.getId(), pessoa.getNomeCompleto());

        return convertToComparecimentoResponse(savedHistorico, proximoComparecimento);
    }

    /**
     * Registrar comparecimento virtual
     */
    public ComparecimentoResponse registrarVirtual(RegistrarComparecimentoVirtualRequest request) {
        // 1. Buscar pessoa
        PessoaMonitorada pessoa = pessoaRepository.findById(request.getPessoaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + request.getPessoaId()));

        // 2. Validar se não há comparecimento duplicado no dia
        validateComparecimentoDuplicado(request.getPessoaId(), LocalDate.now());

        // 3. Validar dados específicos do virtual
        validateComparecimentoVirtual(request);

        // 4. Criar histórico de comparecimento
        HistoricoComparecimento historico = new HistoricoComparecimento();
        historico.setPessoaMonitorada(pessoa);
        historico.setDataComparecimento(LocalDate.now());
        historico.setHoraComparecimento(LocalTime.now());
        historico.setTipoValidacao(TipoValidacao.ONLINE);
        historico.setValidadoPor(request.getValidadoPor());

        // Montar observações com detalhes do virtual
        String observacoes = buildObservacoesVirtual(request);
        historico.setObservacoes(observacoes);

        HistoricoComparecimento savedHistorico = historicoRepository.save(historico);

        // 5. Atualizar regime e status
        LocalDate proximoComparecimento = calcularProximoComparecimento(pessoa);
        atualizarRegimeComparecimento(pessoa, proximoComparecimento);

        pessoa.setStatus(StatusComparecimento.EM_CONFORMIDADE);
        pessoaRepository.save(pessoa);

        // 6. Enviar notificação
        // TODO: notificationService.sendComparecimentoVirtualRegistradoNotification(pessoa, savedHistorico);

        return convertToComparecimentoResponse(savedHistorico, proximoComparecimento);
    }

    /**
     * Registrar justificativa de ausência
     */
    public ComparecimentoResponse registrarJustificativa(RegistrarJustificativaRequest request) {
        // 1. Buscar pessoa
        PessoaMonitorada pessoa = pessoaRepository.findById(request.getPessoaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + request.getPessoaId()));

        // 2. Validar data da ausência
        validateDataAusencia(request.getDataAusencia());

        // 3. Verificar se não há justificativa duplicada para a data
        validateJustificativaDuplicada(request.getPessoaId(), request.getDataAusencia());

        // 4. Criar histórico de justificativa
        HistoricoComparecimento historico = new HistoricoComparecimento();
        historico.setPessoaMonitorada(pessoa);
        historico.setDataComparecimento(request.getDataAusencia());
        historico.setHoraComparecimento(LocalTime.now());
        historico.setTipoValidacao(TipoValidacao.JUSTIFICADO);
        historico.setValidadoPor(request.getValidadoPor());

        // Montar observações com detalhes da justificativa
        String observacoes = buildObservacoesJustificativa(request);
        historico.setObservacoes(observacoes);

        HistoricoComparecimento savedHistorico = historicoRepository.save(historico);

        // 5. Reagendar próximo comparecimento se solicitado
        LocalDate proximoComparecimento = null;
        if (request.isReagendarProximo()) {
            proximoComparecimento = calcularProximoComparecimento(pessoa);
            atualizarRegimeComparecimento(pessoa, proximoComparecimento);

            // Atualizar status para em conformidade
            pessoa.setStatus(StatusComparecimento.EM_CONFORMIDADE);
            pessoaRepository.save(pessoa);
        }

        // 6. Enviar notificação
        // TODO: notificationService.sendJustificativaRegistradaNotification(pessoa, savedHistorico);

        return convertToComparecimentoResponse(savedHistorico, proximoComparecimento);
    }

    /**
     * Buscar histórico de comparecimentos de uma pessoa
     */
    @Transactional(readOnly = true)
    public Page<HistoricoComparecimentoResponse> findHistoricoByPessoa(Long pessoaId, int page, int size,
                                                                       String sortBy, String sortDir) {

        // 1. Verificar se pessoa existe
        if (!pessoaRepository.existsById(pessoaId)) {
            throw new ResourceNotFoundException("Pessoa não encontrada com ID: " + pessoaId);
        }

        // 2. Configurar paginação
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 3. Buscar histórico
        Page<HistoricoComparecimento> historicoPage = historicoRepository.findByPessoaMonitoradaId(pessoaId, pageable);

        // 4. Converter para DTO
        return historicoPage.map(this::convertToHistoricoResponse);
    }

    /**
     * Atualizar próximo comparecimento
     */
    public void atualizarProximoComparecimento(Long pessoaId, AtualizarProximoComparecimentoRequest request) {
        // 1. Buscar pessoa
        PessoaMonitorada pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + pessoaId));

        // 2. Validar nova data
        validateNovaDataComparecimento(request.getNovaData());

        // 3. Atualizar regime de comparecimento
        atualizarRegimeComparecimento(pessoa, request.getNovaData());

        // 4. Registrar histórico da alteração
        registrarAlteracaoComparecimento(pessoa, request);

        // 5. Enviar notificação
        // TODO: notificationService.sendComparecimentoReagendadoNotification(pessoa, request.getNovaData());
    }

    /**
     * Gerar relatório de comparecimentos
     */
    @Transactional(readOnly = true)
    public RelatorioComparecimentoResponse gerarRelatorio(LocalDate dataInicio, LocalDate dataFim,
                                                          String comarca, TipoValidacao tipoValidacao) {

        // 1. Validar período
        validatePeriodoRelatorio(dataInicio, dataFim);

        // 2. Buscar dados para relatório
        Long totalComparecimentos = historicoRepository.countByPeriodo(dataInicio, dataFim, comarca, tipoValidacao);
        Long totalPresenciais = historicoRepository.countByPeriodoAndTipo(dataInicio, dataFim, comarca, TipoValidacao.PRESENCIAL);
        Long totalVirtuais = historicoRepository.countByPeriodoAndTipo(dataInicio, dataFim, comarca, TipoValidacao.ONLINE);
        Long totalJustificativas = historicoRepository.countByPeriodoAndTipo(dataInicio, dataFim, comarca, TipoValidacao.JUSTIFICADO);

        // 3. Calcular estatísticas
        Long totalPessoas = pessoaRepository.countByComarca(comarca);
        Double percentualConformidade = calcularPercentualConformidade(comarca);

        // 4. Montar resposta
        return new RelatorioComparecimentoResponse(
                dataInicio,
                dataFim,
                totalPessoas.intValue(),
                totalComparecimentos.intValue(),
                totalPresenciais.intValue(),
                totalVirtuais.intValue(),
                totalJustificativas.intValue(),
                percentualConformidade,
                comarca
        );
    }

    /**
     * Buscar comparecimentos por período
     */
    @Transactional(readOnly = true)
    public Page<HistoricoComparecimentoResponse> findByPeriodo(LocalDate dataInicio, LocalDate dataFim,
                                                               TipoValidacao tipoValidacao, String comarca,
                                                               int page, int size) {

        // 1. Configurar paginação
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataComparecimento"));

        // 2. Buscar comparecimentos
        Page<HistoricoComparecimento> historicoPage = historicoRepository.findByPeriodoWithFilters(
                dataInicio, dataFim, tipoValidacao, comarca, pageable);

        // 3. Converter para DTO
        return historicoPage.map(this::convertToHistoricoResponse);
    }

    // === MÉTODOS AUXILIARES ===

    private LocalDate calcularProximoComparecimento(PessoaMonitorada pessoa) {
        RegimeComparecimento regime = pessoa.getRegimeComparecimento();
        if (regime == null || regime.getPeriodicidadeDias() == null) {
            throw new BusinessException("Regime de comparecimento não configurado para a pessoa");
        }

        return LocalDate.now().plusDays(regime.getPeriodicidadeDias());
    }

    private void atualizarRegimeComparecimento(PessoaMonitorada pessoa, LocalDate proximoComparecimento) {
        RegimeComparecimento regime = pessoa.getRegimeComparecimento();
        if (regime != null) {
            regime.setProximoComparecimento(proximoComparecimento);
            regimeRepository.save(regime);
        }
    }

    private void validateComparecimentoDuplicado(Long pessoaId, LocalDate data) {
        boolean existe = historicoRepository.existsByPessoaMonitoradaIdAndDataComparecimento(pessoaId, data);
        if (existe) {
            throw new BusinessException("Já existe um comparecimento registrado para esta pessoa na data: " + data);
        }
    }

    private void validateComparecimentoVirtual(RegistrarComparecimentoVirtualRequest request) {
        if (request.getPlataforma() == null || request.getPlataforma().trim().isEmpty()) {
            throw new BusinessException("Plataforma é obrigatória para comparecimento virtual");
        }

        if (request.getDuracaoMinutos() == null || request.getDuracaoMinutos() < 1) {
            throw new BusinessException("Duração deve ser maior que zero");
        }

        if (request.getDuracaoMinutos() > 480) { // 8 horas
            throw new BusinessException("Duração não pode ser maior que 480 minutos");
        }
    }

    private void validateDataAusencia(LocalDate dataAusencia) {
        if (dataAusencia.isAfter(LocalDate.now())) {
            throw new BusinessException("Data da ausência não pode ser futura");
        }

        if (dataAusencia.isBefore(LocalDate.now().minusDays(30))) {
            throw new BusinessException("Data da ausência não pode ser anterior a 30 dias");
        }
    }

    private void validateJustificativaDuplicada(Long pessoaId, LocalDate dataAusencia) {
        boolean existe = historicoRepository.existsByPessoaMonitoradaIdAndDataComparecimentoAndTipoValidacao(
                pessoaId, dataAusencia, TipoValidacao.JUSTIFICADO);

        if (existe) {
            throw new BusinessException("Já existe uma justificativa registrada para esta data");
        }
    }

    private void validateNovaDataComparecimento(LocalDate novaData) {
        if (novaData.isBefore(LocalDate.now())) {
            throw new BusinessException("Nova data de comparecimento não pode ser anterior a hoje");
        }

        if (novaData.isAfter(LocalDate.now().plusYears(1))) {
            throw new BusinessException("Nova data de comparecimento não pode ser superior a 1 ano");
        }
    }

    private void validatePeriodoRelatorio(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }

        if (dataInicio.isBefore(LocalDate.now().minusYears(5))) {
            throw new BusinessException("Data de início não pode ser anterior a 5 anos");
        }

        if (dataFim.isAfter(LocalDate.now())) {
            throw new BusinessException("Data de fim não pode ser futura");
        }
    }

    private String buildObservacoesVirtual(RegistrarComparecimentoVirtualRequest request) {
        StringBuilder obs = new StringBuilder();
        obs.append("Comparecimento virtual via ").append(request.getPlataforma());
        obs.append(" (Duração: ").append(request.getDuracaoMinutos()).append(" min)");

        if (request.getLinkReuniao() != null && !request.getLinkReuniao().trim().isEmpty()) {
            obs.append(" - Link: ").append(request.getLinkReuniao());
        }

        if (request.getObservacoesVirtual() != null && !request.getObservacoesVirtual().trim().isEmpty()) {
            obs.append(" - ").append(request.getObservacoesVirtual());
        }

        if (request.getObservacoes() != null && !request.getObservacoes().trim().isEmpty()) {
            obs.append(" | ").append(request.getObservacoes());
        }

        return obs.toString();
    }

    private String buildObservacoesJustificativa(RegistrarJustificativaRequest request) {
        StringBuilder obs = new StringBuilder();
        obs.append("Justificativa: ").append(request.getMotivoJustificativa());

        if (request.getDocumentosAnexados() != null && !request.getDocumentosAnexados().isEmpty()) {
            obs.append(" - Documentos anexados: ");
            obs.append(String.join(", ", request.getDocumentosAnexados()));
        }

        return obs.toString();
    }

    private void registrarAlteracaoComparecimento(PessoaMonitorada pessoa, AtualizarProximoComparecimentoRequest request) {
        HistoricoComparecimento historico = new HistoricoComparecimento();
        historico.setPessoaMonitorada(pessoa);
        historico.setDataComparecimento(LocalDate.now());
        historico.setHoraComparecimento(LocalTime.now());
        historico.setTipoValidacao(TipoValidacao.JUSTIFICADO);
        historico.setValidadoPor(request.getValidadoPor());
        historico.setObservacoes("Alteração de data de comparecimento: " + request.getMotivoAlteracao() +
                " - Nova data: " + request.getNovaData());

        historicoRepository.save(historico);
    }

    private Double calcularPercentualConformidade(String comarca) {
        Long totalPessoas = comarca != null ?
                pessoaRepository.countByComarca(comarca) :
                pessoaRepository.count();

        if (totalPessoas == 0) {
            return 0.0;
        }

        Long pessoasConformes = comarca != null ?
                pessoaRepository.countByComarcaAndStatus(comarca, StatusComparecimento.EM_CONFORMIDADE) :
                pessoaRepository.countByStatus(StatusComparecimento.EM_CONFORMIDADE);

        return (pessoasConformes.doubleValue() / totalPessoas.doubleValue()) * 100.0;
    }

    private ComparecimentoResponse convertToComparecimentoResponse(HistoricoComparecimento historico,
                                                                   LocalDate proximoComparecimento) {
        return new ComparecimentoResponse(
                historico.getId(),
                historico.getPessoaMonitorada().getId(),
                historico.getDataComparecimento(),
                historico.getHoraComparecimento(),
                historico.getTipoValidacao(),
                historico.getValidadoPor(),
                historico.getObservacoes(),
                proximoComparecimento
        );
    }

    private HistoricoComparecimentoResponse convertToHistoricoResponse(HistoricoComparecimento historico) {
        return new HistoricoComparecimentoResponse(
                historico.getId(),
                historico.getPessoaMonitorada().getId(),
                historico.getPessoaMonitorada().getNomeCompleto(),
                historico.getDataComparecimento(),
                historico.getHoraComparecimento(),
                historico.getTipoValidacao(),
                historico.getValidadoPor(),
                historico.getObservacoes(),
                historico.getCriadoEm()
        );
    }
}