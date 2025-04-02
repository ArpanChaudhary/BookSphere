package com.booksphere.service;

import com.booksphere.dto.TransactionDto;
import com.booksphere.model.Book;
import com.booksphere.model.Transaction;
import com.booksphere.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing transactions.
 */
public interface TransactionService {

    /**
     * Find a transaction by ID.
     * 
     * @param id The transaction ID
     * @return The transaction
     */
    Transaction findById(Long id);

    /**
     * Find all transactions with pagination.
     * 
     * @param pageable Pagination information
     * @return A page of transactions
     */
    Page<Transaction> findAll(Pageable pageable);

    /**
     * Issue a book to a user.
     * 
     * @param userId The user ID
     * @param bookId The book ID
     * @param dueDate The due date
     * @return The created transaction
     */
    Transaction issueBook(Long userId, Long bookId, LocalDateTime dueDate);

    /**
     * Return a book.
     * 
     * @param transactionId The transaction ID
     * @return The updated transaction
     */
    Transaction returnBook(Long transactionId);

    /**
     * Find transactions by user.
     * 
     * @param user The user
     * @param pageable Pagination information
     * @return A page of transactions for the user
     */
    Page<Transaction> findByUser(User user, Pageable pageable);

    /**
     * Find transactions by book.
     * 
     * @param book The book
     * @param pageable Pagination information
     * @return A page of transactions for the book
     */
    Page<Transaction> findByBook(Book book, Pageable pageable);

    /**
     * Find active rentals (issued but not returned) for a user.
     * 
     * @param user The user
     * @return A list of active rental transactions
     */
    List<Transaction> findActiveRentals(User user);

    /**
     * Find overdue transactions.
     * 
     * @return A list of overdue transactions
     */
    List<Transaction> findOverdueTransactions();

    /**
     * Find transactions between dates.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return A page of transactions between the given dates
     */
    Page<Transaction> findTransactionsBetweenDates(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    /**
     * Find transactions for books by a specific author.
     * 
     * @param authorId The author ID
     * @param pageable Pagination information
     * @return A page of transactions for books by the author
     */
    Page<Transaction> findTransactionsByAuthor(Long authorId, Pageable pageable);
    
    /**
     * Calculate late fee for an overdue transaction.
     * 
     * @param transactionId The transaction ID
     * @return The calculated late fee
     */
    Transaction calculateLateFee(Long transactionId);
    
    /**
     * Pay transaction fees.
     * 
     * @param transactionId The transaction ID
     * @return The updated transaction
     */
    Transaction payFees(Long transactionId);

    List<Transaction> findActiveTransactionsByUser(User user);
    List<Transaction> findCompletedTransactionsByUser(User user);
    Transaction createTransaction(User user, Long bookId, int rentalDays);
    Transaction returnBook(Transaction transaction);
    double calculateLateFee(Transaction transaction);
    long countActiveRentals();
    long countOverdueBooks();
}
