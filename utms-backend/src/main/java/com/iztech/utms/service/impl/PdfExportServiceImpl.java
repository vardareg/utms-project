package com.iztech.utms.service.impl;

import com.iztech.utms.service.EvaluationService.RankingDTO;
import com.iztech.utms.service.EvaluationService.RankingResponse;
import com.iztech.utms.service.ExportService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
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

            // Font Settings (using built-in for simplicity, ideally load custom font for
            // Turkish)
            // Note: OpenPDF's HELVETICA supports basic Turkish chars in some encodings,
            // but for full support we strictly need a TTF file (e.g. Arial).
            // For this draft, we use IDENTITY_H with a standard font if available or
            // default.
            // Using standard helvetica with CP1254 (Turkish) encoding if possible.
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, "Cp1254", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 12, Font.BOLD, Color.WHITE);
            Font bodyFont = new Font(baseFont, 12, Font.NORMAL);

            // Title
            Paragraph title = new Paragraph("IZTECH Undergraduate Transfer Ranking", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Department: " + data.getDepartmentName(), bodyFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Table
            PdfPTable table = new PdfPTable(5); // Rank, Name, Score, Status, YKS
            table.setWidthPercentage(100);
            table.setWidths(new int[] { 1, 3, 2, 2, 2 });

            // Headers
            String[] headers = { "Rank", "Candidate Name", "Composite Score", "Status", "Waitlist" };
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

        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void fillTable(PdfPTable table, List<RankingDTO> list, String statusLabel, Font font) {
        if (list == null)
            return;
        for (RankingDTO dto : list) {
            table.addCell(new Phrase(String.valueOf(dto.getRank()), font));
            table.addCell(new Phrase(dto.getStudentName(), font)); // Masking handled in DTO/Service if needed
            table.addCell(new Phrase(dto.getCompositeScore() != null ? dto.getCompositeScore().toString() : "-", font));
            table.addCell(new Phrase(statusLabel, font));
            // Just putting YKS Score or GPA in extra column for info
            table.addCell(new Phrase(dto.getYks() != null ? dto.getYks().toString() : "-", font));
        }
    }
}
