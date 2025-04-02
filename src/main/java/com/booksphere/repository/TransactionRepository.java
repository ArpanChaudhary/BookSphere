package com.booksphere.repository;

import com.booksphere.model.Transaction;
import com.booksphere.model.User;
import com.booksphere.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUser(User user, Pageable pageable);
    Page<Transaction> findByBook(Book book, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.type = 'ISSUE' AND t.returnDate IS NULL")
    List<Transaction> findActiveRentals(@Param("user") User user);
    
    @Query("SELECT t FROM Transaction t WHERE t.issueDate BETWEEN :startDate AND :endDate")
    Page<Transaction> findTransactionsBetweenDates(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.book.author.id = :authorId")
    Page<Transaction> findTransactionsByAuthor(
        @Param("authorId") Long authorId,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user = :user AND t.type = 'ISSUE' AND t.returnDate IS NULL")
    long countActiveRentals(@Param("user") User user);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.book = :book AND t.type = 'ISSUE' AND t.returnDate IS NULL")
    long countActiveRentalsByBook(@Param("book") Book book);

    @Query("SELECT t FROM Transaction t WHERE t.type = 'ISSUE' AND t.returnDate IS NULL AND t.dueDate < :currentDate")
    List<Transaction> findOverdueTransactions(@Param("currentDate") LocalDateTime currentDate);

    List<Transaction> findByUserAndReturnDateIsNull(User user);
    List<Transaction> findByUserAndReturnDateIsNotNull(User user);

    long countByReturnDateIsNull();
    long countByDueDateBeforeAndReturnDateIsNull(LocalDateTime date);
}
