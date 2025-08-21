package com.tjba.comparecimento.service;

import com.tjba.comparecimento.entity.HistoricoComparecimento;
import com.tjba.comparecimento.entity.PessoaMonitorada;
import com.tjba.comparecimento.entity.enums.StatusComparecimento;
import com.tjba.comparecimento.exception.BusinessException;
import com.tjba.comparecimento.repository.HistoricoComparecimentoRepository;
import com.tjba.comparecimento.repository.PessoaMonitoradaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service para geração de relatórios em Excel e PDF.
 */
@Service
@Transactional(readOnly = true)
public class RelatorioService {

    @Autowired
    private HistoricoComparecimentoRepository historicoRepository;

    @Autowired
    private PessoaMonitoradaRepository pessoaRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Gerar relatório de comparecimentos em Excel
     */
    public Resource gerarRelatorioComparecimentosExcel(LocalDate dataInicio, LocalDate dataFim, String comarca) {
        try {
            // 1. Validar período
            validatePeriodo(dataInicio, dataFim);

            // 2. Buscar dados
            List<HistoricoComparecimento> comparecimentos = historicoRepository.findByPeriodoWithFiltersForReport(
                    dataInicio, dataFim, comarca);

            // 3. Criar workbook
            Workbook workbook = new XSSFWorkbook();

            // 4. Criar planilha principal
            Sheet sheet = workbook.createSheet("Comparecimentos");

            // 5. Criar estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle centerStyle = createCenterStyle(workbook);

            // 6. Criar cabeçalho
            createComparecimentosHeader(sheet, headerStyle, dataInicio, dataFim, comarca);

            // 7. Preencher dados
            fillComparecimentosData(sheet, comparecimentos, dateStyle, centerStyle);

            // 8. Auto-ajustar colunas
            autoSizeColumns(sheet, 11);

            // 9. Criar planilha de resumo
            createResumoSheet(workbook, comparecimentos, dataInicio, dataFim, comarca);

            // 10. Converter para bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return new ByteArrayResource(outputStream.toByteArray());

        } catch (IOException e) {
            throw new BusinessException("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    /**
     * Gerar relatório de pessoas em Excel
     */
    public Resource gerarRelatorioPessoasExcel(String comarca, String status) {
        try {
            // 1. Buscar dados
            List<PessoaMonitorada> pessoas = pessoaRepository.findForRelatorio(comarca, status);

            // 2. Criar workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Pessoas Monitoradas");

            // 3. Criar estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle centerStyle = createCenterStyle(workbook);

            // 4. Criar cabeçalho
            createPessoasHeader(sheet, headerStyle, comarca, status);

            // 5. Preencher dados
            fillPessoasData(sheet, pessoas, dateStyle, centerStyle);

            // 6. Auto-ajustar colunas
            autoSizeColumns(sheet, 12);

            // 7. Converter para bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return new ByteArrayResource(outputStream.toByteArray());

        } catch (IOException e) {
            throw new BusinessException("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    /**
     * Gerar relatório de inadimplentes em Excel
     */
    public Resource gerarRelatorioInadimplentesExcel() {
        try {
            // 1. Buscar pessoas inadimplentes
            List<PessoaMonitorada> inadimplentes = pessoaRepository.findByStatus(StatusComparecimento.INADIMPLENTE);

            // 2. Criar workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Pessoas Inadimplentes");

            // 3. Criar estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle centerStyle = createCenterStyle(workbook);
            CellStyle alertStyle = createAlertStyle(workbook);

            // 4. Criar cabeçalho
            createInadimplentesHeader(sheet, headerStyle);

            // 5. Preencher dados
            fillInadimplentesData(sheet, inadimplentes, dateStyle, centerStyle, alertStyle);

            // 6. Auto-ajustar colunas
            autoSizeColumns(sheet, 10);

            // 7. Converter para bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return new ByteArrayResource(outputStream.toByteArray());

        } catch (IOException e) {
            throw new BusinessException("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    /**
     * Gerar relatório estatístico por comarca em Excel
     */
    public Resource gerarRelatorioEstatisticasComarcaExcel(LocalDate dataInicio, LocalDate dataFim) {
        try {
            // 1. Validar período
            validatePeriodo(dataInicio, dataFim);

            // 2. Buscar comarcas
            List<String> comarcas = pessoaRepository.findDistinctComarcas();

            // 3. Criar workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Estatísticas por Comarca");

            // 4. Criar estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle percentStyle = createPercentStyle(workbook);

            // 5. Criar cabeçalho
            createEstatisticasHeader(sheet, headerStyle, dataInicio, dataFim);

            // 6. Preencher dados
            fillEstatisticasData(sheet, comarcas, dataInicio, dataFim, numberStyle, percentStyle);

            // 7. Auto-ajustar colunas
            autoSizeColumns(sheet, 8);

            // 8. Converter para bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return new ByteArrayResource(outputStream.toByteArray());

        } catch (IOException e) {
            throw new BusinessException("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    // === MÉTODOS AUXILIARES ===

    private void validatePeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }

        if (dataInicio.isBefore(LocalDate.now().minusYears(5))) {
            throw new BusinessException("Data de início não pode ser anterior a 5 anos");
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createCenterStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createAlertStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createPercentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("0.00%"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private void createComparecimentosHeader(Sheet sheet, CellStyle headerStyle,
                                             LocalDate dataInicio, LocalDate dataFim, String comarca) {
        // Título
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RELATÓRIO DE COMPARECIMENTOS");
        titleCell.setCellStyle(headerStyle);

        // Período
        Row periodRow = sheet.createRow(1);
        Cell periodCell = periodRow.createCell(0);
        String periodo = "Período: " + dataInicio.format(DATE_FORMATTER) + " a " + dataFim.format(DATE_FORMATTER);
        if (comarca != null) {
            periodo += " - Comarca: " + comarca;
        }
        periodCell.setCellValue(periodo);

        // Headers das colunas
        Row headerRow = sheet.createRow(3);
        String[] headers = {
                "Data", "Hora", "Nome", "CPF", "Tipo", "Validado Por",
                "Processo", "Vara", "Comarca", "Observações"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillComparecimentosData(Sheet sheet, List<HistoricoComparecimento> comparecimentos,
                                         CellStyle dateStyle, CellStyle centerStyle) {
        int rowNum = 4;

        for (HistoricoComparecimento comp : comparecimentos) {
            Row row = sheet.createRow(rowNum++);

            // Data
            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(comp.getDataComparecimento());
            dateCell.setCellStyle(dateStyle);

            // Hora
            Cell timeCell = row.createCell(1);
            if (comp.getHoraComparecimento() != null) {
                timeCell.setCellValue(comp.getHoraComparecimento().toString());
            }
            timeCell.setCellStyle(centerStyle);

            // Nome
            row.createCell(2).setCellValue(comp.getPessoaMonitorada().getNomeCompleto());

            // CPF
            Cell cpfCell = row.createCell(3);
            cpfCell.setCellValue(comp.getPessoaMonitorada().getCpf());
            cpfCell.setCellStyle(centerStyle);

            // Tipo
            Cell tipoCell = row.createCell(4);
            tipoCell.setCellValue(comp.getTipoValidacao().getLabel());
            tipoCell.setCellStyle(centerStyle);

            // Validado por
            row.createCell(5).setCellValue(comp.getValidadoPor());

            // Processo
            ProcessoJudicial processo = comp.getPessoaMonitorada().getProcessoJudicial();
            if (processo != null) {
                row.createCell(6).setCellValue(processo.getNumeroProcesso());
                row.createCell(7).setCellValue(processo.getVara());
                row.createCell(8).setCellValue(processo.getComarca());
            }

            // Observações
            row.createCell(9).setCellValue(comp.getObservacoes());
        }
    }

    private void createPessoasHeader(Sheet sheet, CellStyle headerStyle, String comarca, String status) {
        // Título
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RELATÓRIO DE PESSOAS MONITORADAS");
        titleCell.setCellStyle(headerStyle);

        // Filtros
        Row filterRow = sheet.createRow(1);
        Cell filterCell = filterRow.createCell(0);
        String filtros = "Filtros aplicados:";
        if (comarca != null) filtros += " Comarca: " + comarca;
        if (status != null) filtros += " Status: " + status;
        filterCell.setCellValue(filtros);

        // Headers das colunas
        Row headerRow = sheet.createRow(3);
        String[] headers = {
                "Nome", "CPF", "RG", "Contato", "Status", "Processo",
                "Vara", "Comarca", "Próximo Comparecimento", "Periodicidade", "Observações"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillPessoasData(Sheet sheet, List<PessoaMonitorada> pessoas,
                                 CellStyle dateStyle, CellStyle centerStyle) {
        int rowNum = 4;

        for (PessoaMonitorada pessoa : pessoas) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(pessoa.getNomeCompleto());

            Cell cpfCell = row.createCell(1);
            cpfCell.setCellValue(pessoa.getCpf());
            cpfCell.setCellStyle(centerStyle);

            row.createCell(2).setCellValue(pessoa.getRg());
            row.createCell(3).setCellValue(pessoa.getContato());

            Cell statusCell = row.createCell(4);
            statusCell.setCellValue(pessoa.getStatus().getLabel());
            statusCell.setCellStyle(centerStyle);

            ProcessoJudicial processo = pessoa.getProcessoJudicial();
            if (processo != null) {
                row.createCell(5).setCellValue(processo.getNumeroProcesso());
                row.createCell(6).setCellValue(processo.getVara());
                row.createCell(7).setCellValue(processo.getComarca());
            }

            RegimeComparecimento regime = pessoa.getRegimeComparecimento();
            if (regime != null) {
                Cell proximoCell = row.createCell(8);
                if (regime.getProximoComparecimento() != null) {
                    proximoCell.setCellValue(regime.getProximoComparecimento());
                    proximoCell.setCellStyle(dateStyle);
                }

                row.createCell(9).setCellValue(regime.getPeriodicidadeDescricao());
            }

            row.createCell(10).setCellValue(pessoa.getObservacoes());
        }
    }

    private void createInadimplentesHeader(Sheet sheet, CellStyle headerStyle) {
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RELATÓRIO DE PESSOAS INADIMPLENTES");
        titleCell.setCellStyle(headerStyle);

        Row headerRow = sheet.createRow(2);
        String[] headers = {
                "Nome", "CPF", "Contato", "Processo", "Comarca",
                "Último Comparecimento", "Próximo Comparecimento", "Dias em Atraso", "Observações"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillInadimplentesData(Sheet sheet, List<PessoaMonitorada> inadimplentes,
                                       CellStyle dateStyle, CellStyle centerStyle, CellStyle alertStyle) {
        int rowNum = 3;
        LocalDate hoje = LocalDate.now();

        for (PessoaMonitorada pessoa : inadimplentes) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(pessoa.getNomeCompleto());

            Cell cpfCell = row.createCell(1);
            cpfCell.setCellValue(pessoa.getCpf());
            cpfCell.setCellStyle(centerStyle);

            row.createCell(2).setCellValue(pessoa.getContato());

            ProcessoJudicial processo = pessoa.getProcessoJudicial();
            if (processo != null) {
                row.createCell(3).setCellValue(processo.getNumeroProcesso());
                row.createCell(4).setCellValue(processo.getComarca());
            }

            RegimeComparecimento regime = pessoa.getRegimeComparecimento();
            if (regime != null) {
                // Último comparecimento
                HistoricoComparecimento ultimo = historicoRepository.findLastComparecimentoByPessoa(pessoa.getId());
                if (ultimo != null) {
                    Cell ultimoCell = row.createCell(5);
                    ultimoCell.setCellValue(ultimo.getDataComparecimento());
                    ultimoCell.setCellStyle(dateStyle);
                }

                // Próximo comparecimento
                Cell proximoCell = row.createCell(6);
                if (regime.getProximoComparecimento() != null) {
                    proximoCell.setCellValue(regime.getProximoComparecimento());
                    proximoCell.setCellStyle(dateStyle);

                    // Dias em atraso
                    if (regime.getProximoComparecimento().isBefore(hoje)) {
                        long diasAtraso = regime.getDiasAtraso();
                        Cell atrasoCell = row.createCell(7);
                        atrasoCell.setCellValue(diasAtraso);
                        atrasoCell.setCellStyle(alertStyle);
                    }
                }
            }

            row.createCell(8).setCellValue(pessoa.getObservacoes());
        }
    }

    private void createEstatisticasHeader(Sheet sheet, CellStyle headerStyle,
                                          LocalDate dataInicio, LocalDate dataFim) {
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("ESTATÍSTICAS POR COMARCA");
        titleCell.setCellStyle(headerStyle);

        Row periodRow = sheet.createRow(1);
        Cell periodCell = periodRow.createCell(0);
        periodCell.setCellValue("Período: " + dataInicio.format(DATE_FORMATTER) +
                " a " + dataFim.format(DATE_FORMATTER));

        Row headerRow = sheet.createRow(3);
        String[] headers = {
                "Comarca", "Total Pessoas", "Em Conformidade", "Inadimplentes",
                "% Conformidade", "Comparecimentos Período", "Taxa Comparecimento"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillEstatisticasData(Sheet sheet, List<String> comarcas, LocalDate dataInicio, LocalDate dataFim,
                                      CellStyle numberStyle, CellStyle percentStyle) {
        int rowNum = 4;

        for (String comarca : comarcas) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(comarca);

            // Estatísticas da comarca
            Long totalPessoas = pessoaRepository.countByComarca(comarca);
            Long emConformidade = pessoaRepository.countByComarcaAndStatus(comarca, StatusComparecimento.EM_CONFORMIDADE);
            Long inadimplentes = pessoaRepository.countByComarcaAndStatus(comarca, StatusComparecimento.INADIMPLENTE);
            Long comparecimentosPeriodo = historicoRepository.countByPeriodo(dataInicio, dataFim, comarca, null);

            Cell totalCell = row.createCell(1);
            totalCell.setCellValue(totalPessoas);
            totalCell.setCellStyle(numberStyle);

            Cell conformeCell = row.createCell(2);
            conformeCell.setCellValue(emConformidade);
            conformeCell.setCellStyle(numberStyle);

            Cell inadimCell = row.createCell(3);
            inadimCell.setCellValue(inadimplentes);
            inadimCell.setCellStyle(numberStyle);

            Cell percentCell = row.createCell(4);
            if (totalPessoas > 0) {
                double percent = emConformidade.doubleValue() / totalPessoas.doubleValue();
                percentCell.setCellValue(percent);
            } else {
                percentCell.setCellValue(0);
            }
            percentCell.setCellStyle(percentStyle);

            Cell compCell = row.createCell(5);
            compCell.setCellValue(comparecimentosPeriodo);
            compCell.setCellStyle(numberStyle);

            // Taxa de comparecimento seria calculada baseada nos comparecimentos esperados vs realizados
            Cell taxaCell = row.createCell(6);
            taxaCell.setCellValue("N/A"); // Implementar cálculo conforme necessário
        }
    }

    private void createResumoSheet(Workbook workbook, List<HistoricoComparecimento> comparecimentos,
                                   LocalDate dataInicio, LocalDate dataFim, String comarca) {
        Sheet resumoSheet = workbook.createSheet("Resumo");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);

        // Título
        Row titleRow = resumoSheet.createRow(0);
        titleRow.createCell(0).setCellValue("RESUMO EXECUTIVO");
        titleRow.getCell(0).setCellStyle(headerStyle);

        // Estatísticas
        int rowNum = 2;

        // Total de comparecimentos
        Row totalRow = resumoSheet.createRow(rowNum++);
        totalRow.createCell(0).setCellValue("Total de Comparecimentos:");
        Cell totalCell = totalRow.createCell(1);
        totalCell.setCellValue(comparecimentos.size());
        totalCell.setCellStyle(numberStyle);

        // Por tipo
        long presenciais = comparecimentos.stream()
                .filter(c -> c.getTipoValidacao() == TipoValidacao.PRESENCIAL)
                .count();
        long virtuais = comparecimentos.stream()
                .filter(c -> c.getTipoValidacao() == TipoValidacao.ONLINE)
                .count();
        long justificativas = comparecimentos.stream()
                .filter(c -> c.getTipoValidacao() == TipoValidacao.JUSTIFICADO)
                .count();

        Row presRow = resumoSheet.createRow(rowNum++);
        presRow.createCell(0).setCellValue("Comparecimentos Presenciais:");
        Cell presCell = presRow.createCell(1);
        presCell.setCellValue(presenciais);
        presCell.setCellStyle(numberStyle);

        Row virtRow = resumoSheet.createRow(rowNum++);
        virtRow.createCell(0).setCellValue("Comparecimentos Virtuais:");
        Cell virtCell = virtRow.createCell(1);
        virtCell.setCellValue(virtuais);
        virtCell.setCellStyle(numberStyle);

        Row justRow = resumoSheet.createRow(rowNum++);
        justRow.createCell(0).setCellValue("Justificativas:");
        Cell justCell = justRow.createCell(1);
        justCell.setCellValue(justificativas);
        justCell.setCellStyle(numberStyle);

        // Auto-ajustar colunas
        autoSizeColumns(resumoSheet, 2);
    }

    private void autoSizeColumns(Sheet sheet, int numColumns) {
        for (int i = 0; i < numColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}