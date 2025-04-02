package com.booksphere.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing a book rental transaction in the BookSphere system.
 */
@Data
@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(name = "rental_price", nullable = false)
    private BigDecimal rentalPrice;

    @Column(name = "late_fee")
    private BigDecimal lateFee;

    @Column(name = "paid")
    private boolean paid;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Checks if the book is overdue.
     * 
     * @return true if the book is overdue, false otherwise
     */
    public boolean isOverdue() {
        return type == TransactionType.ISSUE 
            && returnDate == null 
            && dueDate != null 
            && LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * Calculates the late fee based on days overdue.
     * 
     * @return The calculated late fee
     */
    public BigDecimal calculateLateFee() {
        if (!isOverdue()) {
            return BigDecimal.ZERO;
        }
        
        long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDateTime.now());
        // $1 per day late fee
        return BigDecimal.ONE.multiply(BigDecimal.valueOf(daysOverdue));
    }

    /**
     * Enum representing the type of transaction.
     */
    public enum TransactionType {
        ISSUE, RETURN
    }
}
