package com.booksphere.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing a book rental transaction in the BookSphere system.
 */
@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDateTime issueDate;

    @Column
    private LocalDateTime dueDate;

    @Column
    private LocalDateTime returnDate;

    @Column
    private BigDecimal fee;

    @Column
    private BigDecimal lateFee;

    @Column
    private boolean isPaid;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
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
        return new BigDecimal(daysOverdue).multiply(new BigDecimal("1.00"));
    }

    /**
     * Enum representing the type of transaction.
     */
    public enum TransactionType {
        ISSUE, RETURN
    }
}
