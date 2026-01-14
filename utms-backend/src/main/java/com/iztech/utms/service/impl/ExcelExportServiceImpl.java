package com.iztech.utms.service.impl;

import com.iztech.utms.service.EvaluationService.RankingDTO;
import com.iztech.utms.service.EvaluationService.RankingResponse;
import com.iztech.utms.service.ExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service("xlsxExportService")
public class ExcelExportServiceImpl implements ExportService {

    @Override
    public ByteArrayInputStream export(RankingResponse data) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Ranking List");

            // Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = { "Rank", "Candidate Name", "Composite Score", "Status", "YKS Score", "GPA" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;

            // Primary List
            rowIdx = fillRows(sheet, data.getPrimaryList(), "PRIMARY", rowIdx);

            // Wait List
            rowIdx = fillRows(sheet, data.getWaitList(), "WAITLIST", rowIdx);

            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }

    private int fillRows(Sheet sheet, List<RankingDTO> list, String statusLabel, int startRow) {
        if (list == null)
            return startRow;
        int rowIdx = startRow;
        for (RankingDTO dto : list) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(dto.getRank());
            row.createCell(1).setCellValue(dto.getStudentName());
            row.createCell(2).setCellValue(dto.getCompositeScore() != null ? dto.getCompositeScore().doubleValue() : 0);
            row.createCell(3).setCellValue(statusLabel);
            row.createCell(4).setCellValue(dto.getYks() != null ? dto.getYks().doubleValue() : 0);
            row.createCell(5).setCellValue(dto.getGpa() != null ? dto.getGpa().doubleValue() : 0);
        }
        return rowIdx;
    }
}
