package com.booksphere.service.impl;

import com.booksphere.dto.ReportDto;
import com.booksphere.dto.BookStatisticsDto;
import com.booksphere.dto.AuthorStatisticsDto;
import com.booksphere.exception.ResourceNotFoundException;
import com.booksphere.model.Book;
import com.booksphere.model.Transaction;
import com.booksphere.model.User;
import com.booksphere.repository.BookRepository;
import com.booksphere.repository.TransactionRepository;
import com.booksphere.repository.UserRepository;
import com.booksphere.service.ReportService;
import com.booksphere.util.PDFGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the ReportService interface.
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PDFGenerator pdfGenerator;

    @Override
    @Transactional(readOnly = true)
    public ReportDto generateUserTransactionReport(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Transaction> transactions = transactionRepository.findByUser(user, PageRequest.of(0, 1000))
                .stream()
                .filter(t -> t.getCreatedAt().isAfter(startDate) && t.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());
        
        ReportDto reportDto = new ReportDto();
        reportDto.setTitle("Transaction Report for " + user.getFullName());
        reportDto.setSubtitle("Period: " + formatDate(startDate) + " to " + formatDate(endDate));
        
        List<Map<String, String>> data = new ArrayList<>();
        BigDecimal totalRentalFees = BigDecimal.ZERO;
        BigDecimal totalLateFees = BigDecimal.ZERO;
        
        for (Transaction transaction : transactions) {
            Map<String, String> row = new HashMap<>();
            row.put("ID", transaction.getId().toString());
            row.put("Book", transaction.getBook().getTitle());
            row.put("Type", transaction.getType().toString());
            row.put("Issue Date", formatDate(transaction.getIssueDate()));
            row.put("Due Date", formatDate(transaction.getDueDate()));
            row.put("Return Date", transaction.getReturnDate() != null ? formatDate(transaction.getReturnDate()) : "Not returned");
            row.put("Rental Price", "$" + transaction.getRentalPrice());
            row.put("Late Fee", transaction.getLateFee() != null ? "$" + transaction.getLateFee() : "N/A");
            row.put("Paid", transaction.isPaid() ? "Yes" : "No");
            
            data.add(row);
            
            totalRentalFees = totalRentalFees.add(transaction.getRentalPrice());
            
            if (transaction.getLateFee() != null) {
                totalLateFees = totalLateFees.add(transaction.getLateFee());
            }
        }
        
        reportDto.setData(data);
        
        Map<String, String> summary = new HashMap<>();
        summary.put("Total Transactions", String.valueOf(transactions.size()));
        summary.put("Total Rental Fees", "$" + totalRentalFees);
        summary.put("Total Late Fees", "$" + totalLateFees);
        summary.put("Total Amount", "$" + totalRentalFees.add(totalLateFees));
        
        reportDto.setSummary(summary);
        
        return reportDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto generateAllTransactionsReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findTransactionsBetweenDates(
                startDate, endDate, PageRequest.of(0, 1000)).getContent();
        
        ReportDto reportDto = new ReportDto();
        reportDto.setTitle("All Transactions Report");
        reportDto.setSubtitle("Period: " + formatDate(startDate) + " to " + formatDate(endDate));
        
        List<Map<String, String>> data = new ArrayList<>();
        BigDecimal totalRentalFees = BigDecimal.ZERO;
        BigDecimal totalLateFees = BigDecimal.ZERO;
        
        for (Transaction transaction : transactions) {
            Map<String, String> row = new HashMap<>();
            row.put("ID", transaction.getId().toString());
            row.put("User", transaction.getUser().getFullName());
            row.put("Book", transaction.getBook().getTitle());
            row.put("Type", transaction.getType().toString());
            row.put("Issue Date", formatDate(transaction.getIssueDate()));
            row.put("Due Date", formatDate(transaction.getDueDate()));
            row.put("Return Date", transaction.getReturnDate() != null ? formatDate(transaction.getReturnDate()) : "Not returned");
            row.put("Rental Price", "$" + transaction.getRentalPrice());
            row.put("Late Fee", transaction.getLateFee() != null ? "$" + transaction.getLateFee() : "N/A");
            row.put("Paid", transaction.isPaid() ? "Yes" : "No");
            
            data.add(row);
            
            totalRentalFees = totalRentalFees.add(transaction.getRentalPrice());
            
            if (transaction.getLateFee() != null) {
                totalLateFees = totalLateFees.add(transaction.getLateFee());
            }
        }
        
        reportDto.setData(data);
        
        Map<String, String> summary = new HashMap<>();
        summary.put("Total Transactions", String.valueOf(transactions.size()));
        summary.put("Total Rental Fees", "$" + totalRentalFees);
        summary.put("Total Late Fees", "$" + totalLateFees);
        summary.put("Total Amount", "$" + totalRentalFees.add(totalLateFees));
        
        reportDto.setSummary(summary);
        
        return reportDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto generateBookPopularityReport(int limit) {
        List<Book> popularBooks = bookRepository.findMostPopular(limit);
        
        ReportDto reportDto = new ReportDto();
        reportDto.setTitle("Book Popularity Report");
        reportDto.setSubtitle("Top " + limit + " Most Rented Books");
        
        List<Map<String, String>> data = new ArrayList<>();
        
        for (int i = 0; i < popularBooks.size(); i++) {
            Book book = popularBooks.get(i);
            Map<String, String> row = new HashMap<>();
            row.put("Rank", String.valueOf(i + 1));
            row.put("Title", book.getTitle());
            row.put("Author", book.getAuthor() != null ? book.getAuthor().getFullName() : "Unknown");
            row.put("ISBN", book.getIsbn());
            row.put("Published Year", String.valueOf(book.getPublishedYear()));
            row.put("Total Copies", String.valueOf(book.getTotalCopies()));
            row.put("Available Copies", String.valueOf(book.getAvailableCopies()));
            
            data.add(row);
        }
        
        reportDto.setData(data);
        
        return reportDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto generateOverdueReport() {
        List<Transaction> overdueTransactions = transactionRepository.findOverdueTransactions(LocalDateTime.now());
        
        ReportDto reportDto = new ReportDto();
        reportDto.setTitle("Overdue Books Report");
        reportDto.setSubtitle("Generated on " + formatDate(LocalDateTime.now()));
        
        List<Map<String, String>> data = new ArrayList<>();
        
        for (Transaction transaction : overdueTransactions) {
            Map<String, String> row = new HashMap<>();
            row.put("Transaction ID", transaction.getId().toString());
            row.put("User", transaction.getUser().getFullName());
            row.put("Email", transaction.getUser().getEmail());
            row.put("Book", transaction.getBook().getTitle());
            row.put("Issue Date", formatDate(transaction.getIssueDate()));
            row.put("Due Date", formatDate(transaction.getDueDate()));
            row.put("Days Overdue", String.valueOf(
                    java.time.temporal.ChronoUnit.DAYS.between(transaction.getDueDate(), LocalDateTime.now())));
            row.put("Late Fee", "$" + transaction.calculateLateFee());
            
            data.add(row);
        }
        
        reportDto.setData(data);
        
        Map<String, String> summary = new HashMap<>();
        summary.put("Total Overdue Books", String.valueOf(overdueTransactions.size()));
        
        BigDecimal totalLateFees = overdueTransactions.stream()
                .map(Transaction::calculateLateFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        summary.put("Total Late Fees", "$" + totalLateFees);
        
        reportDto.setSummary(summary);
        
        return reportDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto generateRevenueReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findTransactionsBetweenDates(
                startDate, endDate, PageRequest.of(0, 1000)).getContent();
        
        ReportDto reportDto = new ReportDto();
        reportDto.setTitle("Revenue Report");
        reportDto.setSubtitle("Period: " + formatDate(startDate) + " to " + formatDate(endDate));
        
        Map<String, BigDecimal> revenueByMonth = new HashMap<>();
        BigDecimal totalRentalFees = BigDecimal.ZERO;
        BigDecimal totalLateFees = BigDecimal.ZERO;
        
        for (Transaction transaction : transactions) {
            String month = transaction.getCreatedAt().getMonth().toString() + " " + transaction.getCreatedAt().getYear();
            
            BigDecimal rentalFee = transaction.getRentalPrice();
            BigDecimal lateFee = transaction.getLateFee() != null ? transaction.getLateFee() : BigDecimal.ZERO;
            
            revenueByMonth.put(month, revenueByMonth.getOrDefault(month, BigDecimal.ZERO).add(rentalFee).add(lateFee));
            
            totalRentalFees = totalRentalFees.add(rentalFee);
            totalLateFees = totalLateFees.add(lateFee);
        }
        
        List<Map<String, String>> data = new ArrayList<>();
        
        for (Map.Entry<String, BigDecimal> entry : revenueByMonth.entrySet()) {
            Map<String, String> row = new HashMap<>();
            row.put("Month", entry.getKey());
            row.put("Revenue", "$" + entry.getValue());
            
            data.add(row);
        }
        
        reportDto.setData(data);
        
        Map<String, String> summary = new HashMap<>();
        summary.put("Total Rental Fees", "$" + totalRentalFees);
        summary.put("Total Late Fees", "$" + totalLateFees);
        summary.put("Total Revenue", "$" + totalRentalFees.add(totalLateFees));
        
        reportDto.setSummary(summary);
        
        return reportDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto generateAuthorRevenueReport(Long authorId, LocalDateTime startDate, LocalDateTime endDate) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        
        List<Book> authorBooks = bookRepository.findByAuthor(author, PageRequest.of(0, 1000)).getContent();
        List<Transaction> transactions = transactionRepository.findTransactionsBetweenDates(
                startDate, endDate, PageRequest.of(0, 1000)).getContent()
                .stream()
                .filter(t -> authorBooks.stream().anyMatch(b -> b.getId().equals(t.getBook().getId())))
                .collect(Collectors.toList());
        
        ReportDto reportDto = new ReportDto();
        reportDto.setTitle("Revenue Report for " + author.getFullName());
        reportDto.setSubtitle("Period: " + formatDate(startDate) + " to " + formatDate(endDate));
        
        List<Map<String, String>> data = new ArrayList<>();
        BigDecimal totalRentalFees = BigDecimal.ZERO;
        BigDecimal totalLateFees = BigDecimal.ZERO;
        
        for (Transaction transaction : transactions) {
            Map<String, String> row = new HashMap<>();
            row.put("Book", transaction.getBook().getTitle());
            row.put("Rental Price", "$" + transaction.getRentalPrice());
            row.put("Late Fee", transaction.getLateFee() != null ? "$" + transaction.getLateFee() : "N/A");
            
            data.add(row);
            
            totalRentalFees = totalRentalFees.add(transaction.getRentalPrice());
            
            if (transaction.getLateFee() != null) {
                totalLateFees = totalLateFees.add(transaction.getLateFee());
            }
        }
        
        reportDto.setData(data);
        
        Map<String, String> summary = new HashMap<>();
        summary.put("Total Books", String.valueOf(authorBooks.size()));
        summary.put("Total Transactions", String.valueOf(transactions.size()));
        summary.put("Total Rental Fees", "$" + totalRentalFees);
        summary.put("Total Late Fees", "$" + totalLateFees);
        summary.put("Total Revenue", "$" + totalRentalFees.add(totalLateFees));
        
        reportDto.setSummary(summary);
        
        return reportDto;
    }

    @Override
    public byte[] generatePdfReport(ReportDto reportDto) {
        return pdfGenerator.generateReport(reportDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BookStatisticsDto getBookRentalStatistics(Long bookId, LocalDate startDate, LocalDate endDate) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findTransactionsBetweenDates(
                start, end, PageRequest.of(0, 1000)).getContent()
                .stream()
                .filter(t -> t.getBook().getId().equals(bookId))
                .collect(Collectors.toList());

        BookStatisticsDto stats = new BookStatisticsDto();
        stats.setBookId(bookId);
        stats.setBookTitle(book.getTitle());
        stats.setStartDate(startDate);
        stats.setEndDate(endDate);

        stats.setTotalRentals((long) transactions.size());
        stats.setActiveRentals(transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.ISSUE && t.getReturnDate() == null)
                .count());
        stats.setCompletedRentals(transactions.stream()
                .filter(t -> t.getReturnDate() != null)
                .count());
        stats.setOverdueRentals(transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.ISSUE && t.getReturnDate() == null && t.isOverdue())
                .count());

        BigDecimal rentalRevenue = transactions.stream()
                .map(t -> t.getRentalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lateFeeRevenue = transactions.stream()
                .map(t -> t.getLateFee() != null ? t.getLateFee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setRentalRevenue(rentalRevenue);
        stats.setLateFeeRevenue(lateFeeRevenue);
        stats.setTotalRevenue(rentalRevenue.add(lateFeeRevenue));

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public BookStatisticsDto getBookRevenueStatistics(Long bookId, LocalDate startDate, LocalDate endDate) {
        return getBookRentalStatistics(bookId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorStatisticsDto getAuthorOverallStatistics(Long authorId, LocalDate startDate, LocalDate endDate) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Book> authorBooks = bookRepository.findByAuthor(author, PageRequest.of(0, 1000)).getContent();
        List<Transaction> transactions = transactionRepository.findTransactionsBetweenDates(
                start, end, PageRequest.of(0, 1000)).getContent()
                .stream()
                .filter(t -> authorBooks.stream().anyMatch(b -> b.getId().equals(t.getBook().getId())))
                .collect(Collectors.toList());

        AuthorStatisticsDto stats = new AuthorStatisticsDto();
        stats.setAuthorId(authorId);
        stats.setAuthorName(author.getFullName());
        stats.setStartDate(startDate);
        stats.setEndDate(endDate);

        stats.setTotalBooks((long) authorBooks.size());
        stats.setTotalRentals((long) transactions.size());
        stats.setActiveRentals(transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.ISSUE && t.getReturnDate() == null)
                .count());
        stats.setCompletedRentals(transactions.stream()
                .filter(t -> t.getReturnDate() != null)
                .count());
        stats.setOverdueRentals(transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.ISSUE && t.getReturnDate() == null && t.isOverdue())
                .count());

        BigDecimal rentalRevenue = transactions.stream()
                .map(t -> t.getRentalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lateFeeRevenue = transactions.stream()
                .map(t -> t.getLateFee() != null ? t.getLateFee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setRentalRevenue(rentalRevenue);
        stats.setLateFeeRevenue(lateFeeRevenue);
        stats.setTotalRevenue(rentalRevenue.add(lateFeeRevenue));

        // Get statistics for each book
        List<BookStatisticsDto> bookStats = authorBooks.stream()
                .map(book -> getBookRentalStatistics(book.getId(), startDate, endDate))
                .collect(Collectors.toList());

        stats.setBookStatistics(bookStats);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorStatisticsDto getAuthorRevenueStatistics(Long authorId, LocalDate startDate, LocalDate endDate) {
        return getAuthorOverallStatistics(authorId, startDate, endDate);
    }

    /**
     * Format a LocalDateTime to a readable string.
     * 
     * @param dateTime The date/time to format
     * @return The formatted date string
     */
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
