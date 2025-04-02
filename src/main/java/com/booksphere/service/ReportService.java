package com.booksphere.service;

import com.booksphere.dto.ReportDto;
import com.booksphere.dto.BookStatisticsDto;
import com.booksphere.dto.AuthorStatisticsDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service interface for generating reports.
 */
public interface ReportService {

    /**
     * Generate a transaction report for a user.
     * 
     * @param userId The user ID
     * @param startDate The start date
     * @param endDate The end date
     * @return The report data
     */
    ReportDto generateUserTransactionReport(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Generate a transaction report for all users.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return The report data
     */
    ReportDto generateAllTransactionsReport(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Generate a popularity report for books.
     * 
     * @param limit The maximum number of books to include
     * @return The report data
     */
    ReportDto generateBookPopularityReport(int limit);

    /**
     * Generate an overdue books report.
     * 
     * @return The report data
     */
    ReportDto generateOverdueReport();

    /**
     * Generate a revenue report.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return The report data
     */
    ReportDto generateRevenueReport(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Generate a revenue report for an author's books.
     * 
     * @param authorId The author ID
     * @param startDate The start date
     * @param endDate The end date
     * @return The report data
     */
    ReportDto generateAuthorRevenueReport(Long authorId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Generate a PDF report from report data.
     * 
     * @param reportDto The report data
     * @return The PDF file as a byte array
     */
    byte[] generatePdfReport(ReportDto reportDto);

    /**
     * Get rental statistics for a book.
     * 
     * @param bookId The book ID
     * @param startDate The start date
     * @param endDate The end date
     * @return The book statistics
     */
    BookStatisticsDto getBookRentalStatistics(Long bookId, LocalDate startDate, LocalDate endDate);

    /**
     * Get revenue statistics for a book.
     * 
     * @param bookId The book ID
     * @param startDate The start date
     * @param endDate The end date
     * @return The book statistics
     */
    BookStatisticsDto getBookRevenueStatistics(Long bookId, LocalDate startDate, LocalDate endDate);

    /**
     * Get overall statistics for an author.
     * 
     * @param authorId The author ID
     * @param startDate The start date
     * @param endDate The end date
     * @return The author statistics
     */
    AuthorStatisticsDto getAuthorOverallStatistics(Long authorId, LocalDate startDate, LocalDate endDate);

    /**
     * Get revenue statistics for an author.
     * 
     * @param authorId The author ID
     * @param startDate The start date
     * @param endDate The end date
     * @return The author statistics
     */
    AuthorStatisticsDto getAuthorRevenueStatistics(Long authorId, LocalDate startDate, LocalDate endDate);
}
