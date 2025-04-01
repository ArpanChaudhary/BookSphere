package com.booksphere.util;

import com.booksphere.dto.ReportDto;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for generating PDF reports using OpenPDF.
 */
@Component
public class PDFGenerator {

    /**
     * Generate a PDF report from report data.
     * 
     * @param reportDto The report data
     * @return The PDF as a byte array
     */
    public byte[] generateReport(ReportDto reportDto) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            
            document.open();
            addMetadata(document);
            
            // Add title and subtitle
            addTitle(document, reportDto.getTitle(), reportDto.getSubtitle());
            
            // Add table with data
            addTable(document, reportDto);
            
            // Add summary section
            if (reportDto.getSummary() != null && !reportDto.getSummary().isEmpty()) {
                addSummary(document, reportDto.getSummary());
            }
            
            // Add footer
            addFooter(document);
            
            document.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }

    /**
     * Add metadata to the PDF document.
     * 
     * @param document The PDF document
     */
    private void addMetadata(Document document) {
        document.addTitle("BookSphere Report");
        document.addSubject("BookSphere Library Management System");
        document.addKeywords("Books, Library, Report");
        document.addAuthor("BookSphere System");
        document.addCreator("BookSphere PDF Generator");
    }

    /**
     * Add title and subtitle to the PDF document.
     * 
     * @param document The PDF document
     * @param title The report title
     * @param subtitle The report subtitle
     */
    private void addTitle(Document document, String title, String subtitle) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, Font.BOLD, 18, Color.BLACK);
        Paragraph titleParagraph = new Paragraph(title, titleFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(titleParagraph);
        
        if (subtitle != null && !subtitle.isEmpty()) {
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);
            Paragraph subtitleParagraph = new Paragraph(subtitle, subtitleFont);
            subtitleParagraph.setAlignment(Element.ALIGN_CENTER);
            subtitleParagraph.setSpacingAfter(20);
            document.add(subtitleParagraph);
        } else {
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
        }
    }

    /**
     * Add a table with data to the PDF document.
     * 
     * @param document The PDF document
     * @param reportDto The report data
     */
    private void addTable(Document document, ReportDto reportDto) throws DocumentException {
        if (reportDto.getData() == null || reportDto.getData().isEmpty()) {
            Font font = FontFactory.getFont(FontFactory.HELVETICA, Font.ITALIC, 12, Color.DARK_GRAY);
            Paragraph noDatatParagraph = new Paragraph("No data available for this report.", font);
            noDatatParagraph.setAlignment(Element.ALIGN_CENTER);
            noDatatParagraph.setSpacingAfter(20);
            document.add(noDatatParagraph);
            return;
        }
        
        // Get column names from the first data row
        List<String> columnNames = new ArrayList<>(reportDto.getData().get(0).keySet());
        
        // Create table
        PdfPTable table = new PdfPTable(columnNames.size());
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        
        // Add table header
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, Font.BOLD, 10, Color.WHITE);
        Color headerBackground = new Color(66, 139, 202); // Bootstrap primary blue
        
        for (String columnName : columnNames) {
            PdfPCell cell = new PdfPCell(new Phrase(columnName, headerFont));
            cell.setBackgroundColor(headerBackground);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        
        // Add data rows
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        Color evenRowColor = new Color(245, 245, 245); // Light gray
        
        for (int i = 0; i < reportDto.getData().size(); i++) {
            Map<String, String> rowData = reportDto.getData().get(i);
            
            for (String columnName : columnNames) {
                String value = rowData.getOrDefault(columnName, "");
                PdfPCell cell = new PdfPCell(new Phrase(value, dataFont));
                
                // Alternate row colors
                if (i % 2 == 1) {
                    cell.setBackgroundColor(evenRowColor);
                }
                
                cell.setPadding(5);
                table.addCell(cell);
            }
        }
        
        document.add(table);
    }

    /**
     * Add a summary section to the PDF document.
     * 
     * @param document The PDF document
     * @param summary The summary data
     */
    private void addSummary(Document document, Map<String, String> summary) throws DocumentException {
        document.add(new Paragraph(" "));
        Font summaryTitleFont = FontFactory.getFont(FontFactory.HELVETICA, Font.BOLD, 12, Color.BLACK);
        Paragraph summaryTitle = new Paragraph("Summary", summaryTitleFont);
        summaryTitle.setSpacingAfter(10);
        document.add(summaryTitle);
        
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        
        for (Map.Entry<String, String> entry : summary.entrySet()) {
            Paragraph summaryItem = new Paragraph(
                    entry.getKey() + ": " + entry.getValue(), summaryFont);
            summaryItem.setIndentationLeft(20);
            summaryItem.setSpacingAfter(5);
            document.add(summaryItem);
        }
    }

    /**
     * Add a footer to the PDF document.
     * 
     * @param document The PDF document
     */
    private void addFooter(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, Font.ITALIC, 8, Color.DARK_GRAY);
        Paragraph footer = new Paragraph("Generated by BookSphere on " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
}
