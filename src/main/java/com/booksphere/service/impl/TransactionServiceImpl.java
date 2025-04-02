package com.booksphere.service.impl;

import com.booksphere.exception.ResourceNotFoundException;
import com.booksphere.model.Book;
import com.booksphere.model.Notification;
import com.booksphere.model.Transaction;
import com.booksphere.model.User;
import com.booksphere.repository.BookRepository;
import com.booksphere.repository.NotificationRepository;
import com.booksphere.repository.TransactionRepository;
import com.booksphere.repository.UserRepository;
import com.booksphere.service.BookService;
import com.booksphere.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the TransactionService interface.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final NotificationRepository notificationRepository;
    private final BookService bookService;

    @Override
    @Transactional(readOnly = true)
    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Transaction issueBook(Long userId, Long bookId, LocalDateTime dueDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        
        // Check if book is available
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is not available for rental: " + book.getTitle());
        }
        
        // Check if user already has this book
        List<Transaction> activeRentals = transactionRepository.findActiveRentals(user);
        boolean alreadyRented = activeRentals.stream()
                .anyMatch(t -> t.getBook().getId().equals(bookId));
        
        if (alreadyRented) {
            throw new IllegalStateException("User already has this book: " + book.getTitle());
        }
        
        // Create new transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setType(Transaction.TransactionType.ISSUE);
        transaction.setIssueDate(LocalDateTime.now());
        transaction.setDueDate(dueDate);
        transaction.setRentalPrice(book.getRentalPrice());
        transaction.setPaid(false);
        
        // Update book available copies
        book.checkoutCopy();
        bookRepository.save(book);
        
        // Create a notification for the author
        if (book.getAuthor() != null) {
            Notification notification = new Notification(
                book.getAuthor(),
                "Your book '" + book.getTitle() + "' has been rented by " + user.getFullName(),
                Notification.NotificationType.BOOK_AVAILABLE,
                book
            );
            notificationRepository.save(notification);
        }
        
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction returnBook(Long transactionId) {
        Transaction transaction = findById(transactionId);
        
        // Check if already returned
        if (transaction.getReturnDate() != null) {
            throw new IllegalStateException("Book already returned");
        }
        
        // Update transaction
        transaction.setReturnDate(LocalDateTime.now());
        transaction.setType(Transaction.TransactionType.RETURN);
        
        // Calculate late fee if overdue
        if (transaction.isOverdue()) {
            BigDecimal lateFee = transaction.calculateLateFee();
            transaction.setLateFee(lateFee);
        }
        
        // Update book available copies
        Book book = transaction.getBook();
        book.returnCopy();
        bookRepository.save(book);
        
        // Create a notification for the user
        Notification notification = new Notification(
            transaction.getUser(),
            "You have successfully returned '" + book.getTitle() + "'.",
            Notification.NotificationType.SYSTEM_NOTIFICATION,
            book
        );
        notificationRepository.save(notification);
        
        // Create a notification for the author
        if (book.getAuthor() != null) {
            Notification authorNotification = new Notification(
                book.getAuthor(),
                "Your book '" + book.getTitle() + "' has been returned by " + transaction.getUser().getFullName(),
                Notification.NotificationType.SYSTEM_NOTIFICATION,
                book
            );
            notificationRepository.save(authorNotification);
        }
        
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findByUser(User user, Pageable pageable) {
        return transactionRepository.findByUser(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findByBook(Book book, Pageable pageable) {
        return transactionRepository.findByBook(book, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findActiveRentals(User user) {
        return transactionRepository.findActiveRentals(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findOverdueTransactions() {
        return transactionRepository.findOverdueTransactions(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findTransactionsBetweenDates(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        return transactionRepository.findTransactionsBetweenDates(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findTransactionsByAuthor(Long authorId, Pageable pageable) {
        return transactionRepository.findTransactionsByAuthor(authorId, pageable);
    }

    @Override
    @Transactional
    public Transaction calculateLateFee(Long transactionId) {
        Transaction transaction = findById(transactionId);
        
        if (transaction.isOverdue()) {
            BigDecimal lateFee = transaction.calculateLateFee();
            transaction.setLateFee(lateFee);
            return transactionRepository.save(transaction);
        }
        
        return transaction;
    }

    @Override
    @Transactional
    public Transaction payFees(Long transactionId) {
        Transaction transaction = findById(transactionId);
        
        // Mark transaction as paid
        transaction.setPaid(true);
        
        // Notify user
        Notification notification = new Notification(
            transaction.getUser(),
            "Payment received for '" + transaction.getBook().getTitle() + "'.",
            Notification.NotificationType.SYSTEM_NOTIFICATION
        );
        notificationRepository.save(notification);
        
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> findActiveTransactionsByUser(User user) {
        return transactionRepository.findByUserAndReturnDateIsNull(user);
    }

    @Override
    public List<Transaction> findCompletedTransactionsByUser(User user) {
        return transactionRepository.findByUserAndReturnDateIsNotNull(user);
    }

    @Override
    @Transactional
    public Transaction createTransaction(User user, Long bookId, int rentalDays) {
        Book book = bookService.findById(bookId);
        
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Book is not available for rental");
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setIssueDate(LocalDateTime.now());
        transaction.setDueDate(LocalDateTime.now().plusDays(rentalDays));
        transaction.setType(Transaction.TransactionType.ISSUE);

        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction returnBook(Transaction transaction) {
        if (transaction.getReturnDate() != null) {
            throw new RuntimeException("Book has already been returned");
        }

        transaction.setReturnDate(LocalDateTime.now());
        transaction.setType(Transaction.TransactionType.RETURN);
        
        // Calculate late fee if applicable
        BigDecimal lateFee = BigDecimal.valueOf(calculateLateFee(transaction));
        transaction.setLateFee(lateFee);

        // Update book availability
        Book book = transaction.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return transactionRepository.save(transaction);
    }

    @Override
    public double calculateLateFee(Transaction transaction) {
        if (transaction.getReturnDate() == null || transaction.getDueDate() == null) {
            return 0.0;
        }

        long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                transaction.getDueDate(),
                transaction.getReturnDate()
        );

        if (daysOverdue <= 0) {
            return 0.0;
        }

        // Calculate late fee: $1 per day overdue
        return daysOverdue;
    }

    @Override
    public long countActiveRentals() {
        return transactionRepository.countByReturnDateIsNull();
    }

    @Override
    public long countOverdueBooks() {
        return transactionRepository.countByDueDateBeforeAndReturnDateIsNull(LocalDateTime.now());
    }
}
