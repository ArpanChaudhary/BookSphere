package com.booksphere.service.impl;

import com.booksphere.dto.ReportDto;
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
        BigDecimal totalFees = BigDecimal.ZERO;
        BigDecimal totalLateFees = BigDecimal.ZERO;
        
        for (Transaction transaction : transactions) {
            Map<String, String> row = new HashMap<>();
            row.put("ID", transaction.getId().toString());
            row.put("Book", transaction.getBook().getTitle());
            row.put("Type", transaction.getType().toString());
            row.put("Issue Date", formatDate(transaction.getIssueDate()));
            row.put("Due Date", formatDate(transaction.getDueDate()));
            row.put("Return Date", transaction.getReturnDate() != null ? formatDate(transaction.getReturnDate()) : "Not returned");
            row.put("Fee", transaction.getFee() != null ? "$" + transaction.getFee() : "N/A");
            row.put("Late Fee", transaction.getLateFee() != null ? "$" + transaction.getLateFee() : "N/A");
            row.put("Paid", transaction.isPaid() ? "Yes" : "No");
            
            data.add(row);
            
            if (transaction.getFee() != null) {
                totalFees = totalFees.add(transaction.getFee());
            }
            
            if (transaction.getLateFee() != null) {
                totalLateFees = totalLateFees.add(transaction.getLateFee());
            }
        }
        
        reportDto.setData(data);
        
        Map<String, String> summary = new HashMap<>();
        summary.put("Total Transactions", String.valueOf(transactions.size()));
        summary.put("Total Rental Fees", "$" + totalFees);
        summary.put("Total Late Fees", "$" + totalLateFees);
        summary.put("Total Amount", "$" + totalFees.add(totalLateFees));
        
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
        BigDecimal totalFees = BigDecimal.ZERO;
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
            row.put("Fee", transaction.getFee() != null ? "$" + transaction.getFee() : "N/A");
            row.put("Late Fee", transaction.getLateFee() != null ? "$" + transaction.getLateFee() : "N/A");
            row.put("Paid", transaction.isPaid() ? "Yes" : "No");
            
            data.add(row);
            
            if (transaction.getFee() != null) {
                totalFees = totalFees.add(transaction.getFee());
            }
            
            if (transaction.getLateFee() != null) {
                totalLateFees = totalLateFees.add(transaction.getLateFee());
            }
        }
        
        reportDto.setData(data);
        
        Map<String, String> summary = new HashMap<>();
        summary.put("Total Transactions", String.valueOf(transactions.size()));
        summary.put("Total Rental Fees", "$" + totalFees);
        summary.put("Total Late Fees", "$" + totalLateFees);
        summary.put("Total Amount", "$" + totalFees.add(totalLateFees));
        
        reportDto.setSummary(summary);
        
        return reportDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto generateBookPopularityReport(int limit) {
        List<Book> popularBooks = bookRepository.findMostRentedBooks(limit);
        
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
            row.put("Publication Year", String.valueOf(book.getPublicationYear()));
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
            
            BigDecimal fee = transaction.getFee() != null ? transaction.getFee() : BigDecimal.ZERO;
            BigDecimal lateFee = transaction.getLateFee() != null ? transaction.getLateFee() : BigDecimal.ZERO;
            
            revenueByMonth.put(month, revenueByMonth.getOrDefault(month, BigDecimal.ZERO).add(fee).add(lateFee));
            
            totalRentalFees = totalRentalFees.add(fee);
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
        summary.put("Total Transactions", String.valueOf(transactions.size()));
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
        
        List<Transaction> transactions = transactionRepository.findTransactionsByAuthor(
                authorId, PageRequest.of(0, 1000)).getContent()
                .stream()
                .filter(t -> t.getCreatedAt().isAfter(startDate) && t.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());
        
        ReportDto reportDto = new ReportDto();
        reportDto.setTitle("Author Revenue Report for " + author.getFullName());
        reportDto.setSubtitle("Period: " + formatDate(startDate) + " to " + formatDate(endDate));
        
        Map<Book, List<Transaction>> transactionsByBook = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getBook));
        
        List<Map<String, String>> data = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        
        for (Map.Entry<Book, List<Transaction>> entry : transactionsByBook.entrySet()) {
            Book book = entry.getKey();
            List<Transaction> bookTransactions = entry.getValue();
            
            BigDecimal bookRevenue = bookTransactions.stream()
                    .map(t -> {
                        BigDecimal fee = t.getFee() != null ? t.getFee() : BigDecimal.ZERO;
                        BigDecimal lateFee = t.getLateFee() != null ? t.getLateFee() : BigDecimal.ZERO;
                        return fee.add(lateFee);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Map<String, String> row = new HashMap<>();
            row.put("Book Title", book.getTitle());
            row.put("ISBN", book.getIsbn());
            row.put("Rental Count", String.valueOf(bookTransactions.size()));
            row.put("Revenue", "$" + bookRevenue);
            
            data.add(row);
            
            totalRevenue = totalRevenue.add(bookRevenue);
        }
        
        reportDto.setData(data);
        
        Map<String, String> summary = new HashMap<>();
        summary.put("Total Books", String.valueOf(transactionsByBook.size()));
        summary.put("Total Rentals", String.valueOf(transactions.size()));
        summary.put("Total Revenue", "$" + totalRevenue);
        
        reportDto.setSummary(summary);
        
        return reportDto;
    }

    @Override
    public byte[] generatePdfReport(ReportDto reportDto) {
        return pdfGenerator.generateReport(reportDto);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }
}
