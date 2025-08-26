package com.tjba.comparecimento.service;

import com.tjba.comparecimento.dto.response.*;
import com.tjba.comparecimento.entity.enums.StatusComparecimento;
import com.tjba.comparecimento.entity.enums.TipoValidacao;
import com.tjba.comparecimento.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Service para dashboard e estatísticas do sistema.
 */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    @Autowired
    private PessoaMonitoradaRepository pessoaRepository;

    @Autowired
    private HistoricoComparecimentoRepository historicoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProcessoJudicialRepository processoRepository;

    // TODO: Injetar AuditService quando implementar
    // @Autowired private AuditService auditService;

    /**
     * Obter estatísticas gerais do dashboard
     */
    public EstatisticasGeraisResponse getEstatisticasGerais() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);

        // 1. Estatísticas de pessoas
        long totalPessoas = pessoaRepository.count();
        long emConformidade = pessoaRepository.countByStatus(StatusComparecimento.EM_CONFORMIDADE);
        long inadimplentes = pessoaRepository.countByStatus(StatusComparecimento.INADIMPLENTE);

        // 2. Comparecimentos hoje
        long comparecimentosHoje = pessoaRepository.countByProximoComparecimento(hoje);

        // 3. Pessoas atrasadas (próximo comparecimento vencido)
        long atrasados = pessoaRepository.countByProximoComparecimentoVencido(hoje);

        // 4. Comparecimentos este mês
        long comparecimentosMes = historicoRepository.countByPeriodo(inicioMes, hoje, null, null);

        // 5. Novos cadastros este mês - Corrigido para usar LocalDateTime
        LocalDateTime inicioMesDateTime = inicioMes.atStartOfDay();
        LocalDateTime hojeDateTime = hoje.atTime(23, 59, 59);
        long novosCadastrosMes = pessoaRepository.countByPeriodoCriacao(inicioMesDateTime, hojeDateTime);

        // 6. Percentual de conformidade
        Double percentualConformidade = totalPessoas > 0 ?
                ((double) emConformidade / (double) totalPessoas) * 100.0 : 0.0;

        return new EstatisticasGeraisResponse(
                (int) totalPessoas,
                (int) emConformidade,
                (int) inadimplentes,
                (int) comparecimentosHoje,
                (int) atrasados,
                (int) comparecimentosMes,
                Math.round(percentualConformidade * 100.0) / 100.0,
                (int) novosCadastrosMes
        );
    }

    /**
     * Obter estatísticas por comarca
     */
    public List<EstatisticaComarcaResponse> getEstatisticasPorComarca() {
        // 1. Buscar todas as comarcas distintas
        List<String> comarcas = processoRepository.findDistinctComarcas();

        // 2. Calcular estatísticas para cada comarca
        return comarcas.stream()
                .map(this::calcularEstatisticasComarca)
                .collect(Collectors.toList());
    }

    /**
     * Obter comparecimentos dos próximos dias
     */
    public List<ProximoComparecimentoResponse> getProximosComparecimentos(int dias) {
        LocalDate hoje = LocalDate.now();
        List<ProximoComparecimentoResponse> proximos = new ArrayList<>();

        for (int i = 0; i < dias; i++) {
            LocalDate data = hoje.plusDays(i);
            long quantidade = pessoaRepository.countByProximoComparecimento(data);

            // Criar ProximoComparecimentoResponse usando construtor padrão e setters
            ProximoComparecimentoResponse proximoComparecimento = new ProximoComparecimentoResponse();
            proximoComparecimento.setDataInicio(data);
            proximoComparecimento.setTotalComparecimentos((int) quantidade);
            
            proximos.add(proximoComparecimento);
        }

        return proximos;
    }

    /**
     * Obter dados para gráfico de comparecimentos por mês
     */
    public GraficoComparecimentosResponse getGraficoComparecimentos(int meses) {
        List<String> labels = new ArrayList<>();
        List<Integer> dadosPresenciais = new ArrayList<>();
        List<Integer> dadosVirtuais = new ArrayList<>();
        List<Integer> dadosJustificativas = new ArrayList<>();

        LocalDate dataAtual = LocalDate.now().minusMonths(meses - 1).withDayOfMonth(1);

        for (int i = 0; i < meses; i++) {
            LocalDate inicioMes = dataAtual;
            LocalDate fimMes = dataAtual.withDayOfMonth(dataAtual.lengthOfMonth());

            // Label do mês
            String label = dataAtual.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR")) +
                    "/" + dataAtual.getYear();
            labels.add(label);

            // Dados por tipo de comparecimento
            long presenciais = historicoRepository.countByPeriodoAndTipo(inicioMes, fimMes, null, TipoValidacao.PRESENCIAL);
            long virtuais = historicoRepository.countByPeriodoAndTipo(inicioMes, fimMes, null, TipoValidacao.ONLINE);
            long justificativas = historicoRepository.countByPeriodoAndTipo(inicioMes, fimMes, null, TipoValidacao.JUSTIFICADO);

            dadosPresenciais.add((int) presenciais);
            dadosVirtuais.add((int) virtuais);
            dadosJustificativas.add((int) justificativas);

            dataAtual = dataAtual.plusMonths(1);
        }

        return new GraficoComparecimentosResponse(
                labels,
                dadosPresenciais,
                dadosVirtuais,
                dadosJustificativas
        );
    }

    /**
     * Obter alertas do sistema
     */
    public List<AlertaResponse> getAlertas() {
        List<AlertaResponse> alertas = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        // 1. Alerta de pessoas em atraso
        long pessoasAtrasadas = pessoaRepository.countByProximoComparecimentoVencido(hoje);
        if (pessoasAtrasadas > 0) {
            alertas.add(new AlertaResponse(
                    "warning",
                    "Pessoas em atraso",
                    pessoasAtrasadas + " pessoas estão com comparecimentos em atraso",
                    hoje,
                    "high"
            ));
        }

        // 2. Alerta de comparecimentos hoje
        long comparecimentosHoje = pessoaRepository.countByProximoComparecimento(hoje);
        if (comparecimentosHoje > 0) {
            alertas.add(new AlertaResponse(
                    "info",
                    "Comparecimentos hoje",
                    comparecimentosHoje + " pessoas devem comparecer hoje",
                    hoje,
                    "medium"
            ));
        }

        // 3. Alerta de comparecimentos próximos (próximos 3 dias)
        long proximosComparecimentos = pessoaRepository.countByProximoComparecimentoEntre(
                hoje.plusDays(1), hoje.plusDays(3));
        if (proximosComparecimentos > 0) {
            alertas.add(new AlertaResponse(
                    "info",
                    "Próximos comparecimentos",
                    proximosComparecimentos + " pessoas devem comparecer nos próximos 3 dias",
                    hoje,
                    "low"
            ));
        }

        // 4. Alerta de alta inadimplência por comarca
        List<String> comarcasAltaInadimplencia = findComarcasAltaInadimplencia();
        for (String comarca : comarcasAltaInadimplencia) {
            alertas.add(new AlertaResponse(
                    "danger",
                    "Alta inadimplência",
                    "Comarca " + comarca + " com alta taxa de inadimplência",
                    hoje,
                    "high"
            ));
        }

        // 5. Alerta de sistema - exemplo: backups, atualizações, etc.
        // TODO: Implementar alertas de sistema quando necessário

        return alertas;
    }

    /**
     * Obter atividades recentes do sistema
     */
    public List<AtividadeRecenteResponse> getAtividadesRecentes(int limite) {
        List<AtividadeRecenteResponse> atividades = new ArrayList<>();

        // 1. Últimos comparecimentos registrados - Corrigido para usar Pageable
        Pageable pageable = PageRequest.of(0, limite / 2);
        List<Object[]> ultimosComparecimentos = historicoRepository.findUltimosComparecimentos(pageable);
        for (Object[] row : ultimosComparecimentos) {
            String nomePessoa = (String) row[0];
            TipoValidacao tipo = (TipoValidacao) row[1];
            String validadoPor = (String) row[2];
            LocalDateTime dataHora = (LocalDateTime) row[3];

            String tipoDescricao = switch (tipo) {
                case PRESENCIAL -> "presencialmente";
                case ONLINE -> "virtualmente";
                case JUSTIFICADO -> "justificativa de ausência";
            };

            atividades.add(new AtividadeRecenteResponse(
                    "comparecimento",
                    nomePessoa + " compareceu " + tipoDescricao,
                    validadoPor,
                    dataHora
            ));
        }

        // 2. Últimas pessoas cadastradas - Corrigido para usar Pageable
        Pageable pageablePessoas = PageRequest.of(0, limite / 2);
        List<Object[]> ultimasPessoas = pessoaRepository.findUltimasPessoas(pageablePessoas);
        for (Object[] row : ultimasPessoas) {
            String nomePessoa = (String) row[0];
            LocalDateTime dataCriacao = (LocalDateTime) row[1];

            atividades.add(new AtividadeRecenteResponse(
                    "cadastro",
                    "Nova pessoa cadastrada: " + nomePessoa,
                    "Sistema",
                    dataCriacao
            ));
        }

        // 3. Ordenar por data e limitar resultado
        return atividades.stream()
                .sorted((a, b) -> b.getDataHora().compareTo(a.getDataHora()))
                .limit(limite)
                .collect(Collectors.toList());
    }

    /**
     * Obter estatísticas de performance do sistema
     */
    public PerformanceResponse getPerformanceStats() {
        LocalDate hoje = LocalDate.now();
        LocalDate ultimoMes = hoje.minusMonths(1);

        // 1. Taxa de comparecimento no último mês
        long totalEsperados = pessoaRepository.countComparecimentosEsperados(ultimoMes, hoje);
        long totalRealizados = historicoRepository.countByPeriodoExcluindoJustificativas(ultimoMes, hoje, TipoValidacao.JUSTIFICADO);

        Double taxaComparecimento = totalEsperados > 0 ?
                ((double) totalRealizados / (double) totalEsperados) * 100.0 : 0.0;

        // 2. Tempo médio entre comparecimentos
        Double tempoMedioEntre = historicoRepository.calcularTempoMedioEntreComparecimentos(TipoValidacao.JUSTIFICADO);

        // 3. Eficiência por tipo de comparecimento
        long totalPresenciais = historicoRepository.countByPeriodoAndTipo(ultimoMes, hoje, null, TipoValidacao.PRESENCIAL);
        long totalVirtuais = historicoRepository.countByPeriodoAndTipo(ultimoMes, hoje, null, TipoValidacao.ONLINE);

        Double percentualVirtual = (totalPresenciais + totalVirtuais) > 0 ?
                ((double) totalVirtuais / (double) (totalPresenciais + totalVirtuais)) * 100.0 : 0.0;

        return new PerformanceResponse(
                Math.round(taxaComparecimento * 100.0) / 100.0,
                tempoMedioEntre != null ? Math.round(tempoMedioEntre * 100.0) / 100.0 : 0.0,
                Math.round(percentualVirtual * 100.0) / 100.0,
                (int) totalRealizados,
                (int) totalEsperados
        );
    }

    // === MÉTODOS AUXILIARES ===

    private EstatisticaComarcaResponse calcularEstatisticasComarca(String comarca) {
        long totalPessoas = pessoaRepository.countByComarca(comarca);
        long emConformidade = pessoaRepository.countByComarcaAndStatus(comarca, StatusComparecimento.EM_CONFORMIDADE);
        long inadimplentes = pessoaRepository.countByComarcaAndStatus(comarca, StatusComparecimento.INADIMPLENTE);

        Double percentualConformidade = totalPessoas > 0 ?
                ((double) emConformidade / (double) totalPessoas) * 100.0 : 0.0;

        return new EstatisticaComarcaResponse(
                comarca,
                (int) totalPessoas,
                (int) emConformidade,
                (int) inadimplentes,
                Math.round(percentualConformidade * 100.0) / 100.0
        );
    }

    private List<String> findComarcasAltaInadimplencia() {
        List<String> comarcas = processoRepository.findDistinctComarcas();
        List<String> comarcasProblematicas = new ArrayList<>();

        for (String comarca : comarcas) {
            long totalPessoas = pessoaRepository.countByComarca(comarca);
            long inadimplentes = pessoaRepository.countByComarcaAndStatus(comarca, StatusComparecimento.INADIMPLENTE);

            if (totalPessoas > 0) {
                double percentualInadimplencia = ((double) inadimplentes / (double) totalPessoas) * 100.0;

                // Considera alta inadimplência se > 30%
                if (percentualInadimplencia > 30.0) {
                    comarcasProblematicas.add(comarca);
                }
            }
        }

        return comarcasProblematicas;
    }
}