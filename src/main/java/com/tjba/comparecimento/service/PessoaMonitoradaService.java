package com.tjba.comparecimento.service;

import com.tjba.comparecimento.dto.request.CreatePessoaRequest;
import com.tjba.comparecimento.dto.request.UpdatePessoaRequest;
import com.tjba.comparecimento.dto.response.PessoaDetalheResponse;
import com.tjba.comparecimento.dto.response.PessoaResponse;
import com.tjba.comparecimento.entity.*;
import com.tjba.comparecimento.entity.enums.StatusComparecimento;
import com.tjba.comparecimento.exception.BusinessException;
import com.tjba.comparecimento.exception.ResourceNotFoundException;
import com.tjba.comparecimento.repository.*;
import com.tjba.comparecimento.util.CpfUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de pessoas monitoradas.
 */
@Service
@Transactional
public class PessoaMonitoradaService {

    @Autowired
    private PessoaMonitoradaRepository pessoaRepository;

    @Autowired
    private ProcessoJudicialRepository processoRepository;

    @Autowired
    private RegimeComparecimentoRepository regimeRepository;

    @Autowired
    private EnderecoVinculadoRepository enderecoRepository;

    @Autowired
    private HistoricoComparecimentoRepository historicoRepository;

    // TODO: Injetar ViaCepService quando implementar
    // @Autowired private ViaCepService viaCepService;

    // TODO: Injetar NotificationService quando implementar
    // @Autowired private NotificationService notificationService;

    /**
     * Buscar pessoas com filtros e paginação
     */
    @Transactional(readOnly = true)
    public Page<PessoaResponse> findAllWithFilters(int page, int size, String sortBy, String sortDir,
                                                   String nome, String cpf, String rg, StatusComparecimento status,
                                                   String comarca, LocalDate proximoComparecimento) {

        // 1. Configurar paginação e ordenação
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 2. Buscar com filtros
        Page<PessoaMonitorada> pessoasPage = pessoaRepository.findAllWithFilters(
                nome, cpf, rg, status, comarca, proximoComparecimento, pageable);

        // 3. Converter para DTO
        return pessoasPage.map(this::convertToPessoaResponse);
    }

    /**
     * Buscar pessoa por ID com detalhes completos
     */
    @Transactional(readOnly = true)
    public PessoaDetalheResponse findById(Long id) {
        PessoaMonitorada pessoa = pessoaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + id));

        return convertToPessoaDetalheResponse(pessoa);
    }

    /**
     * Cadastrar nova pessoa
     */
    public PessoaResponse createPessoa(CreatePessoaRequest request) {
        // 1. Validar dados únicos
        validateUniqueFields(request.getCpf(), request.getRg(), request.getNumeroProcesso(), null);

        // 2. Validar dados pessoais
        validatePessoaData(request);

        // 3. Criar pessoa
        PessoaMonitorada pessoa = new PessoaMonitorada();
        pessoa.setNomeCompleto(request.getNomeCompleto().trim());
        pessoa.setCpf(request.getCpf());
        pessoa.setRg(request.getRg());
        pessoa.setContato(request.getContato());
        pessoa.setContatoEmergencia(request.getContatoEmergencia());
        pessoa.setStatus(StatusComparecimento.EM_CONFORMIDADE);
        pessoa.setObservacoes(request.getObservacoes());

        // 4. Salvar pessoa primeiro
        PessoaMonitorada savedPessoa = pessoaRepository.save(pessoa);

        // 5. Criar processo judicial
        ProcessoJudicial processo = new ProcessoJudicial();
        processo.setNumeroProcesso(request.getNumeroProcesso());
        processo.setVara(request.getVara());
        processo.setComarca(request.getComarca());
        processo.setDataDecisao(request.getDataDecisao());
        processo.setPessoaMonitorada(savedPessoa);
        processo.setAtivo(true);

        ProcessoJudicial savedProcesso = processoRepository.save(processo);
        savedPessoa.setProcessoJudicial(savedProcesso);

        // 6. Criar regime de comparecimento
        RegimeComparecimento regime = new RegimeComparecimento();
        regime.setPeriodicidadeDias(request.getPeriodicidadeDias());
        regime.setDataComparecimentoInicial(request.getDataComparecimentoInicial());
        regime.setProximoComparecimento(request.getDataComparecimentoInicial());
        regime.setPessoaMonitorada(savedPessoa);

        RegimeComparecimento savedRegime = regimeRepository.save(regime);
        savedPessoa.setRegimeComparecimento(savedRegime);

        // 7. Criar endereço
        EnderecoVinculado endereco = new EnderecoVinculado();
        endereco.setCep(request.getCep());
        endereco.setLogradouro(request.getLogradouro());
        endereco.setNumero(request.getNumero());
        endereco.setComplemento(request.getComplemento());
        endereco.setBairro(request.getBairro());
        endereco.setCidade(request.getCidade());
        endereco.setEstado(request.getEstado());
        endereco.setPessoaMonitorada(savedPessoa);

        EnderecoVinculado savedEndereco = enderecoRepository.save(endereco);
        savedPessoa.setEndereco(savedEndereco);

        // 8. Salvar pessoa atualizada
        savedPessoa = pessoaRepository.save(savedPessoa);

        // 9. Enviar notificação de cadastro
        // TODO: notificationService.sendCadastroNotification(savedPessoa);

        // 10. Log da ação
        // TODO: auditService.logPessoaCreation(savedPessoa.getId(), savedPessoa.getNomeCompleto());

        return convertToPessoaResponse(savedPessoa);
    }

    /**
     * Atualizar dados da pessoa
     */
    public PessoaResponse updatePessoa(Long id, UpdatePessoaRequest request) {
        // 1. Buscar pessoa
        PessoaMonitorada pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + id));

        // 2. Validar dados únicos (excluindo a própria pessoa)
        validateUniqueFields(request.getCpf(), request.getRg(), null, id);

        // 3. Validar dados
        validateUpdatePessoaData(request);

        // 4. Atualizar campos
        pessoa.setNomeCompleto(request.getNomeCompleto().trim());
        pessoa.setCpf(request.getCpf());
        pessoa.setRg(request.getRg());
        pessoa.setContato(request.getContato());
        pessoa.setContatoEmergencia(request.getContatoEmergencia());
        pessoa.setObservacoes(request.getObservacoes());

        if (request.getStatus() != null) {
            pessoa.setStatus(request.getStatus());
        }

        // 5. Salvar alterações
        PessoaMonitorada updatedPessoa = pessoaRepository.save(pessoa);

        // 6. Log da ação
        // TODO: auditService.logPessoaUpdate(updatedPessoa.getId(), updatedPessoa.getNomeCompleto());

        return convertToPessoaResponse(updatedPessoa);
    }

    /**
     * Buscar pessoas com comparecimento hoje
     */
    @Transactional(readOnly = true)
    public List<PessoaResponse> findComparecimentosHoje() {
        LocalDate hoje = LocalDate.now();
        List<PessoaMonitorada> pessoas = pessoaRepository.findByProximoComparecimento(hoje);

        return pessoas.stream()
                .map(this::convertToPessoaResponse)
                .collect(Collectors.toList());
    }

    /**
     * Buscar pessoas em atraso
     */
    @Transactional(readOnly = true)
    public List<PessoaResponse> findPessoasAtrasadas() {
        LocalDate hoje = LocalDate.now();
        List<PessoaMonitorada> pessoas = pessoaRepository.findAtrasadas(hoje);

        return pessoas.stream()
                .map(this::convertToPessoaResponse)
                .collect(Collectors.toList());
    }

    /**
     * Buscar por CPF
     */
    @Transactional(readOnly = true)
    public PessoaResponse findByCpf(String cpf) {
        // 1. Normalizar CPF
        String cpfNormalizado = CpfUtil.normalize(cpf);

        // 2. Validar CPF
        if (!CpfUtil.isValid(cpfNormalizado)) {
            throw new BusinessException("CPF inválido");
        }

        // 3. Buscar pessoa
        PessoaMonitorada pessoa = pessoaRepository.findByCpf(cpfNormalizado)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com CPF: " + cpf));

        return convertToPessoaResponse(pessoa);
    }

    /**
     * Buscar por número do processo
     */
    @Transactional(readOnly = true)
    public PessoaResponse findByNumeroProcesso(String numeroProcesso) {
        PessoaMonitorada pessoa = pessoaRepository.findByProcessoNumero(numeroProcesso)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com processo: " + numeroProcesso));

        return convertToPessoaResponse(pessoa);
    }

    /**
     * Atualizar status automaticamente baseado nas datas
     */
    public void atualizarStatusAutomatico() {
        LocalDate hoje = LocalDate.now();

        // 1. Buscar pessoas com próximo comparecimento vencido
        List<PessoaMonitorada> pessoasVencidas = pessoaRepository.findComProximoComparecimentoVencido(hoje,StatusComparecimento.EM_CONFORMIDADE);

        // 2. Atualizar status para inadimplente
        for (PessoaMonitorada pessoa : pessoasVencidas) {
            if (pessoa.getStatus() == StatusComparecimento.EM_CONFORMIDADE) {
                pessoa.setStatus(StatusComparecimento.INADIMPLENTE);
                pessoaRepository.save(pessoa);

                // 3. Enviar notificação de inadimplência
                // TODO: notificationService.sendInadimplenciaNotification(pessoa);
            }
        }
    }

    /**
     * Buscar pessoas por comarca
     */
    @Transactional(readOnly = true)
    public Page<PessoaResponse> findByComarca(String comarca, Pageable pageable) {
        Page<PessoaMonitorada> pessoas = pessoaRepository.findByComarca(comarca, pageable);
        return pessoas.map(this::convertToPessoaResponse);
    }

    /**
     * Contar pessoas por status
     */
    @Transactional(readOnly = true)
    public long countByStatus(StatusComparecimento status) {
        return pessoaRepository.countByStatus(status);
    }

    /**
     * Buscar próximos vencimentos (próximos N dias)
     */
    @Transactional(readOnly = true)
    public List<PessoaResponse> findProximosVencimentos(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataLimite = hoje.plusDays(dias);

        List<PessoaMonitorada> pessoas = pessoaRepository.findProximosVencimentos(hoje, dataLimite);

        return pessoas.stream()
                .map(this::convertToPessoaResponse)
                .collect(Collectors.toList());
    }

    // === MÉTODOS AUXILIARES ===

    private PessoaResponse convertToPessoaResponse(PessoaMonitorada pessoa) {
        return new PessoaResponse(
                pessoa.getId(),
                pessoa.getNomeCompleto(),
                pessoa.getCpf(),
                pessoa.getRg(),
                pessoa.getContato(),
                pessoa.getStatus(),
                pessoa.getProcessoJudicial() != null ? pessoa.getProcessoJudicial().getNumeroProcesso() : null,
                pessoa.getProcessoJudicial() != null ? pessoa.getProcessoJudicial().getVara() : null,
                pessoa.getProcessoJudicial() != null ? pessoa.getProcessoJudicial().getComarca() : null,
                pessoa.getRegimeComparecimento() != null ? pessoa.getRegimeComparecimento().getProximoComparecimento() : null,
                pessoa.getCriadoEm()
        );
    }

    private PessoaDetalheResponse convertToPessoaDetalheResponse(PessoaMonitorada pessoa) {
        return new PessoaDetalheResponse(
                pessoa.getId(),
                pessoa.getNomeCompleto(),
                pessoa.getCpf(),
                pessoa.getRg(),
                pessoa.getContato(),
                pessoa.getContatoEmergencia(),
                pessoa.getStatus(),
                // Dados do processo
                pessoa.getProcessoJudicial() != null ? pessoa.getProcessoJudicial().getNumeroProcesso() : null,
                pessoa.getProcessoJudicial() != null ? pessoa.getProcessoJudicial().getVara() : null,
                pessoa.getProcessoJudicial() != null ? pessoa.getProcessoJudicial().getComarca() : null,
                pessoa.getProcessoJudicial() != null ? pessoa.getProcessoJudicial().getDataDecisao() : null,
                // Dados do regime
                pessoa.getRegimeComparecimento() != null ? pessoa.getRegimeComparecimento().getPeriodicidadeDias() : null,
                pessoa.getRegimeComparecimento() != null ? pessoa.getRegimeComparecimento().getDataComparecimentoInicial() : null,
                pessoa.getRegimeComparecimento() != null ? pessoa.getRegimeComparecimento().getProximoComparecimento() : null,
                // Dados do endereço
                pessoa.getEndereco() != null ? pessoa.getEndereco().getEnderecoCompleto() : null,
                pessoa.getEndereco() != null ? pessoa.getEndereco().getBairro() : null,
                pessoa.getEndereco() != null ? pessoa.getEndereco().getCidade() : null,
                pessoa.getEndereco() != null ? pessoa.getEndereco().getEstado() : null,
                pessoa.getEndereco() != null ? pessoa.getEndereco().getCep() : null,
                // Outros
                pessoa.getObservacoes(),
                pessoa.getCriadoEm(),
                pessoa.getAtualizadoEm()
        );
    }

    private void validateUniqueFields(String cpf, String rg, String numeroProcesso, Long excludeId) {
        // Validar CPF único
        if (cpf != null && !cpf.trim().isEmpty()) {
            String cpfNormalizado = CpfUtil.normalize(cpf);
            if (!CpfUtil.isValid(cpfNormalizado)) {
                throw new BusinessException("CPF inválido");
            }

            boolean cpfExists = excludeId != null ?
                    pessoaRepository.existsByCpfAndIdNot(cpfNormalizado, excludeId) :
                    pessoaRepository.existsByCpf(cpfNormalizado);

            if (cpfExists) {
                throw new BusinessException("Já existe uma pessoa cadastrada com este CPF");
            }
        }

        // Validar RG único
        if (rg != null && !rg.trim().isEmpty()) {
            boolean rgExists = excludeId != null ?
                    pessoaRepository.existsByRgAndIdNot(rg.trim(), excludeId) :
                    pessoaRepository.existsByRg(rg.trim());

            if (rgExists) {
                throw new BusinessException("Já existe uma pessoa cadastrada com este RG");
            }
        }

        // Validar número do processo único
        if (numeroProcesso != null && !numeroProcesso.trim().isEmpty()) {
            boolean processoExists = excludeId != null ?
                    processoRepository.existsByNumeroProcessoAndPessoaMonitoradaIdNot(numeroProcesso.trim(), excludeId) :
                    processoRepository.existsByNumeroProcesso(numeroProcesso.trim());

            if (processoExists) {
                throw new BusinessException("Já existe um processo cadastrado com este número");
            }
        }
    }

    private void validatePessoaData(CreatePessoaRequest request) {
        // Validar se pelo menos um documento foi fornecido
        if ((request.getCpf() == null || request.getCpf().trim().isEmpty()) &&
                (request.getRg() == null || request.getRg().trim().isEmpty())) {
            throw new BusinessException("Pelo menos CPF ou RG deve ser informado");
        }

        // Validar datas
        if (request.getDataDecisao().isAfter(LocalDate.now())) {
            throw new BusinessException("Data da decisão não pode ser futura");
        }

        if (request.getDataComparecimentoInicial().isBefore(request.getDataDecisao())) {
            throw new BusinessException("Data do primeiro comparecimento não pode ser anterior à data da decisão");
        }

        // Validar periodicidade
        if (request.getPeriodicidadeDias() < 1 || request.getPeriodicidadeDias() > 365) {
            throw new BusinessException("Periodicidade deve estar entre 1 e 365 dias");
        }
    }

    private void validateUpdatePessoaData(UpdatePessoaRequest request) {
        // Validar se pelo menos um documento foi fornecido
        if ((request.getCpf() == null || request.getCpf().trim().isEmpty()) &&
                (request.getRg() == null || request.getRg().trim().isEmpty())) {
            throw new BusinessException("Pelo menos CPF ou RG deve ser informado");
        }
    }
}