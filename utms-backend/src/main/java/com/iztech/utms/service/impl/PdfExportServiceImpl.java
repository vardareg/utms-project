package com.iztech.utms.service.impl;

import com.iztech.utms.service.EvaluationService.RankingDTO;
import com.iztech.utms.service.EvaluationService.RankingResponse;
import com.iztech.utms.service.ExportService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service("pdfExportService")
public class PdfExportServiceImpl implements ExportService {

    @Override
    public ByteArrayInputStream export(RankingResponse data) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Font Settings: Use HELVETICA (Standard Type 1 font) which doesn't require
            // external files
            // This is safe for headless Linux environments, though Turkish char support
            // might be limited without embedded TTF.
            // For MVP, stability is prioritized over perfect character rendering.
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // Title
            Paragraph title = new Paragraph("IZTECH Undergraduate Transfer Ranking", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Department: " + data.getDepartmentName(), bodyFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Table
            PdfPTable table = new PdfPTable(6); // Rank, Name, TCKN, Score, Status, YKS
            table.setWidthPercentage(100);
            table.setWidths(new int[] { 1, 3, 2, 2, 2, 2 });

            // Headers
            String[] headers = { "Rank", "Candidate Name", "TC No", "GPA", "Status", "Total Score" };
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(Color.GRAY);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Primary List
            fillTable(table, data.getPrimaryList(), "PRIMARY", bodyFont);

            // Wait List
            fillTable(table, data.getWaitList(), "WAITLIST", bodyFont);

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void fillTable(PdfPTable table, List<RankingDTO> list, String statusLabel, Font font) {
        if (list == null)
            return;
        for (RankingDTO dto : list) {
            table.addCell(new Phrase(String.valueOf(dto.getRank()), font));
            table.addCell(new Phrase(dto.getFullName(), font));
            table.addCell(new Phrase(dto.getTckn() != null ? dto.getTckn() : "-", font));
            table.addCell(new Phrase(dto.getGpa() != null ? dto.getGpa().toString() : "-", font));
            table.addCell(new Phrase(statusLabel, font));
            // Total Score = Composite Score
            table.addCell(new Phrase(dto.getCompositeScore() != null ? dto.getCompositeScore().toString() : "-", font));
        }
    }
}
