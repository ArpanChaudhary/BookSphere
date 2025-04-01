package com.booksphere.controller;

import com.booksphere.dto.ReportDto;
import com.booksphere.model.User;
import com.booksphere.service.ReportService;
import com.booksphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for generating and downloading reports.
 */
@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    /**
     * Download a transaction report for a user.
     * 
     * @param userDetails The authenticated user details
     * @param startDate The start date
     * @param endDate The end date
     * @return The PDF report as a downloadable file
     */
    @GetMapping("/user-transactions")
    public ResponseEntity<byte[]> downloadUserTransactionReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        User user = userService.findByUsername(userDetails.getUsername());
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        ReportDto reportDto = reportService.generateUserTransactionReport(user.getId(), start, end);
        byte[] pdfBytes = reportService.generatePdfReport(reportDto);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user-transactions-" + 
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /**
     * Download a transaction report for all users (admin function).
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return The PDF report as a downloadable file
     */
    @GetMapping("/all-transactions")
    public ResponseEntity<byte[]> downloadAllTransactionsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        ReportDto reportDto = reportService.generateAllTransactionsReport(start, end);
        byte[] pdfBytes = reportService.generatePdfReport(reportDto);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all-transactions-" + 
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /**
     * Download a book popularity report (admin function).
     * 
     * @param limit The maximum number of books to include
     * @return The PDF report as a downloadable file
     */
    @GetMapping("/book-popularity")
    public ResponseEntity<byte[]> downloadBookPopularityReport(
            @RequestParam(defaultValue = "10") int limit) {
        
        ReportDto reportDto = reportService.generateBookPopularityReport(limit);
        byte[] pdfBytes = reportService.generatePdfReport(reportDto);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=book-popularity-" + 
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /**
     * Download an overdue books report (admin function).
     * 
     * @return The PDF report as a downloadable file
     */
    @GetMapping("/overdue")
    public ResponseEntity<byte[]> downloadOverdueReport() {
        ReportDto reportDto = reportService.generateOverdueReport();
        byte[] pdfBytes = reportService.generatePdfReport(reportDto);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=overdue-books-" + 
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /**
     * Download a revenue report (admin function).
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return The PDF report as a downloadable file
     */
    @GetMapping("/revenue")
    public ResponseEntity<byte[]> downloadRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        ReportDto reportDto = reportService.generateRevenueReport(start, end);
        byte[] pdfBytes = reportService.generatePdfReport(reportDto);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=revenue-report-" + 
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /**
     * Download an author revenue report.
     * 
     * @param userDetails The authenticated user details
     * @param startDate The start date
     * @param endDate The end date
     * @return The PDF report as a downloadable file
     */
    @GetMapping("/author-revenue")
    public ResponseEntity<byte[]> downloadAuthorRevenueReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        User author = userService.findByUsername(userDetails.getUsername());
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        ReportDto reportDto = reportService.generateAuthorRevenueReport(author.getId(), start, end);
        byte[] pdfBytes = reportService.generatePdfReport(reportDto);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=author-revenue-" + 
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
