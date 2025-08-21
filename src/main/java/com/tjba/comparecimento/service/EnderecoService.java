package com.tjba.comparecimento.service;

import com.tjba.comparecimento.dto.request.UpdateEnderecoRequest;
import com.tjba.comparecimento.dto.response.EnderecoResponse;
import com.tjba.comparecimento.dto.response.ViaCepResponse;
import com.tjba.comparecimento.entity.EnderecoVinculado;
import com.tjba.comparecimento.entity.PessoaMonitorada;
import com.tjba.comparecimento.exception.BusinessException;
import com.tjba.comparecimento.exception.ResourceNotFoundException;
import com.tjba.comparecimento.repository.EnderecoVinculadoRepository;
import com.tjba.comparecimento.repository.PessoaMonitoradaRepository;
import com.tjba.comparecimento.util.CepUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * Service para gerenciamento de endereços.
 */
@Service
@Transactional
public class EnderecoService {

    @Autowired
    private EnderecoVinculadoRepository enderecoRepository;

    @Autowired
    private PessoaMonitoradaRepository pessoaRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${viacep.api.url:https://viacep.com.br/ws}")
    private String viaCepApiUrl;

    // TODO: Injetar CacheService quando implementar para cache de consultas CEP
    // @Autowired private CacheService cacheService;

    /**
     * Buscar endereço por CEP usando ViaCEP
     */
    @Transactional(readOnly = true)
    public ViaCepResponse buscarPorCep(String cep) {
        // 1. Validar e normalizar CEP
        String cepNormalizado = CepUtil.normalize(cep);
        if (!CepUtil.isValid(cepNormalizado)) {
            throw new BusinessException("CEP inválido: " + cep);
        }

        try {
            // 2. Verificar cache primeiro
            // TODO: ViaCepResponse cached = cacheService.getCachedCep(cepNormalizado);
            // if (cached != null) return cached;

            // 3. Consultar ViaCEP
            String url = viaCepApiUrl + "/" + cepNormalizado + "/json/";
            ViaCepApiResponse apiResponse = restTemplate.getForObject(url, ViaCepApiResponse.class);

            // 4. Verificar se CEP foi encontrado
            if (apiResponse == null || apiResponse.isErro()) {
                throw new ResourceNotFoundException("CEP não encontrado: " + cep);
            }

            // 5. Converter para response
            ViaCepResponse response = new ViaCepResponse(
                    apiResponse.getCep(),
                    apiResponse.getLogradouro(),
                    apiResponse.getComplemento(),
                    apiResponse.getBairro(),
                    apiResponse.getLocalidade(),
                    apiResponse.getUf(),
                    apiResponse.getIbge(),
                    apiResponse.getGia(),
                    apiResponse.getDdd(),
                    apiResponse.getSiafi()
            );

            // 6. Armazenar no cache
            // TODO: cacheService.cacheCep(cepNormalizado, response);

            return response;

        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException || e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("Erro ao consultar CEP: " + e.getMessage());
        }
    }

    /**
     * Atualizar endereço de uma pessoa
     */
    public EnderecoResponse updateEnderecoPessoa(Long pessoaId, UpdateEnderecoRequest request) {
        // 1. Buscar pessoa
        PessoaMonitorada pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + pessoaId));

        // 2. Validar dados do endereço
        validateEnderecoData(request);

        // 3. Buscar ou criar endereço
        EnderecoVinculado endereco = pessoa.getEndereco();
        if (endereco == null) {
            endereco = new EnderecoVinculado();
            endereco.setPessoaMonitorada(pessoa);
        }

        // 4. Atualizar campos
        endereco.setCep(request.getCep());
        endereco.setLogradouro(request.getLogradouro());
        endereco.setNumero(request.getNumero());
        endereco.setComplemento(request.getComplemento());
        endereco.setBairro(request.getBairro());
        endereco.setCidade(request.getCidade());
        endereco.setEstado(request.getEstado());

        // 5. Salvar endereço
        EnderecoVinculado savedEndereco = enderecoRepository.save(endereco);

        // 6. Associar à pessoa se necessário
        if (pessoa.getEndereco() == null) {
            pessoa.setEndereco(savedEndereco);
            pessoaRepository.save(pessoa);
        }

        // 7. Log da ação
        // TODO: auditService.logEnderecoUpdate(pessoaId, savedEndereco.getId());

        return convertToEnderecoResponse(savedEndereco);
    }

    /**
     * Obter endereço de uma pessoa
     */
    @Transactional(readOnly = true)
    public EnderecoResponse findByPessoaId(Long pessoaId) {
        // 1. Verificar se pessoa existe
        if (!pessoaRepository.existsById(pessoaId)) {
            throw new ResourceNotFoundException("Pessoa não encontrada com ID: " + pessoaId);
        }

        // 2. Buscar endereço
        EnderecoVinculado endereco = enderecoRepository.findByPessoaMonitoradaId(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado para a pessoa"));

        return convertToEnderecoResponse(endereco);
    }

    /**
     * Validar CEP e auto-completar dados quando possível
     */
    public ViaCepResponse validarEAutoCompletarCep(String cep) {
        try {
            ViaCepResponse viaCepData = buscarPorCep(cep);

            // Validar se o CEP retornou dados válidos
            if (viaCepData.getLogradouro() == null || viaCepData.getLogradouro().trim().isEmpty()) {
                throw new BusinessException("CEP encontrado mas sem dados de logradouro");
            }

            return viaCepData;

        } catch (ResourceNotFoundException e) {
            throw new BusinessException("CEP não encontrado nos Correios");
        }
    }

    /**
     * Buscar endereços por cidade
     */
    @Transactional(readOnly = true)
    public List<EnderecoResponse> findByCidade(String cidade) {
        List<EnderecoVinculado> enderecos = enderecoRepository.findByCidadeIgnoreCase(cidade);

        return enderecos.stream()
                .map(this::convertToEnderecoResponse)
                .collect(Collectors.toList());
    }

    /**
     * Buscar endereços por estado
     */
    @Transactional(readOnly = true)
    public List<EnderecoResponse> findByEstado(String estado) {
        // Normalizar estado para maiúscula
        String estadoNormalizado = estado.toUpperCase();

        List<EnderecoVinculado> enderecos = enderecoRepository.findByEstado(estadoNormalizado);

        return enderecos.stream()
                .map(this::convertToEnderecoResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obter estatísticas de endereços por região
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getEstatisticasPorEstado() {
        List<Object[]> resultados = enderecoRepository.countByEstado();

        return resultados.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],  // estado
                        row -> (Long) row[1]     // count
                ));
    }

    /**
     * Obter estatísticas de endereços por cidade
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getEstatisticasPorCidade(String estado) {
        List<Object[]> resultados = enderecoRepository.countByCidadeAndEstado(estado.toUpperCase());

        return resultados.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],  // cidade
                        row -> (Long) row[1]     // count
                ));
    }

    // === MÉTODOS AUXILIARES ===

    private void validateEnderecoData(UpdateEnderecoRequest request) {
        // Validar CEP
        if (request.getCep() == null || request.getCep().trim().isEmpty()) {
            throw new BusinessException("CEP é obrigatório");
        }

        String cepNormalizado = CepUtil.normalize(request.getCep());
        if (!CepUtil.isValid(cepNormalizado)) {
            throw new BusinessException("CEP inválido");
        }

        // Validar logradouro
        if (request.getLogradouro() == null || request.getLogradouro().trim().isEmpty()) {
            throw new BusinessException("Logradouro é obrigatório");
        }

        if (request.getLogradouro().length() > 200) {
            throw new BusinessException("Logradouro deve ter no máximo 200 caracteres");
        }

        // Validar bairro
        if (request.getBairro() == null || request.getBairro().trim().isEmpty()) {
            throw new BusinessException("Bairro é obrigatório");
        }

        // Validar cidade
        if (request.getCidade() == null || request.getCidade().trim().isEmpty()) {
            throw new BusinessException("Cidade é obrigatória");
        }

        // Validar estado
        if (request.getEstado() == null || request.getEstado().trim().isEmpty()) {
            throw new BusinessException("Estado é obrigatório");
        }

        if (!request.getEstado().matches("[A-Z]{2}")) {
            throw new BusinessException("Estado deve ser informado com 2 letras maiúsculas (ex: BA)");
        }

        // Validar campos opcionais
        if (request.getNumero() != null && request.getNumero().length() > 20) {
            throw new BusinessException("Número deve ter no máximo 20 caracteres");
        }

        if (request.getComplemento() != null && request.getComplemento().length() > 100) {
            throw new BusinessException("Complemento deve ter no máximo 100 caracteres");
        }
    }

    private EnderecoResponse convertToEnderecoResponse(EnderecoVinculado endereco) {
        return new EnderecoResponse(
                endereco.getId(),
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getEnderecoCompleto(),
                endereco.getEnderecoResumido(),
                endereco.getPessoaMonitorada().getId()
        );
    }

    // === CLASSE AUXILIAR PARA RESPONSE DA API VIACEP ===

    private static class ViaCepApiResponse {
        private String cep;
        private String logradouro;
        private String complemento;
        private String bairro;
        private String localidade;
        private String uf;
        private String ibge;
        private String gia;
        private String ddd;
        private String siafi;
        private boolean erro;

        // Getters e Setters
        public String getCep() { return cep; }
        public void setCep(String cep) { this.cep = cep; }

        public String getLogradouro() { return logradouro; }
        public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

        public String getComplemento() { return complemento; }
        public void setComplemento(String complemento) { this.complemento = complemento; }

        public String getBairro() { return bairro; }
        public void setBairro(String bairro) { this.bairro = bairro; }

        public String getLocalidade() { return localidade; }
        public void setLocalidade(String localidade) { this.localidade = localidade; }

        public String getUf() { return uf; }
        public void setUf(String uf) { this.uf = uf; }

        public String getIbge() { return ibge; }
        public void setIbge(String ibge) { this.ibge = ibge; }

        public String getGia() { return gia; }
        public void setGia(String gia) { this.gia = gia; }

        public String getDdd() { return ddd; }
        public void setDdd(String ddd) { this.ddd = ddd; }

        public String getSiafi() { return siafi; }
        public void setSiafi(String siafi) { this.siafi = siafi; }

        public boolean isErro() { return erro; }
        public void setErro(boolean erro) { this.erro = erro; }
    }
}