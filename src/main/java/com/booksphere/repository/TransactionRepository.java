package com.booksphere.repository;

import com.booksphere.model.Book;
import com.booksphere.model.Transaction;
import com.booksphere.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Transaction entity.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

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
     * Find transactions by type.
     * 
     * @param type The transaction type
     * @param pageable Pagination information
     * @return A page of transactions of the specified type
     */
    Page<Transaction> findByType(Transaction.TransactionType type, Pageable pageable);

    /**
     * Find active rentals (issued but not returned) for a user.
     * 
     * @param user The user
     * @return A list of active rental transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.type = 'ISSUE' AND t.returnDate IS NULL")
    List<Transaction> findActiveRentals(@Param("user") User user);

    /**
     * Find active rentals for a book.
     * 
     * @param book The book
     * @return A list of active rental transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.book = :book AND t.type = 'ISSUE' AND t.returnDate IS NULL")
    List<Transaction> findActiveRentals(@Param("book") Book book);

    /**
     * Find overdue transactions.
     * 
     * @param now The current date/time
     * @return A list of overdue transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'ISSUE' AND t.dueDate < :now AND t.returnDate IS NULL")
    List<Transaction> findOverdueTransactions(@Param("now") LocalDateTime now);

    /**
     * Find transactions between dates.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return A page of transactions between the given dates
     */
    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    Page<Transaction> findTransactionsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Find transactions for books by a specific author.
     * 
     * @param authorId The author ID
     * @param pageable Pagination information
     * @return A page of transactions for books by the author
     */
    @Query("SELECT t FROM Transaction t JOIN t.book b WHERE b.author.id = :authorId")
    Page<Transaction> findTransactionsByAuthor(@Param("authorId") Long authorId, Pageable pageable);
}
