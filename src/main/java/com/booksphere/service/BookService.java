package com.booksphere.service;

import com.booksphere.dto.BookDto;
import com.booksphere.model.Book;
import com.booksphere.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for managing books.
 */
public interface BookService {
    Book createBook(Book book);
    Page<Book> getAllBooks(Pageable pageable);
    Optional<Book> getBookById(Long id);
    Optional<Book> getBookByIsbn(String isbn);
    Book updateBook(Long id, Book bookDetails);
    void deleteBook(Long id);
    Page<Book> searchBooks(String searchTerm, Pageable pageable);
    List<Book> getBooksByAuthor(Long authorId);
    Page<Book> getAvailableBooks(Pageable pageable);
    Page<Book> getBooksByGenre(String genreName, Pageable pageable);
    Page<Book> getBooksByYear(int year, Pageable pageable);
    Page<Book> getOutOfStockBooks(Pageable pageable);
    Book updateBookStock(Long id, int quantity);
    Book create(BookDto bookDto, User author);
    Page<Book> findAll(Pageable pageable);
    Page<Book> findAvailable(Pageable pageable);
    Page<Book> findByAuthor(User author, Pageable pageable);
    Page<Book> findByGenre(Long genreId, Pageable pageable);
    List<Book> findMostPopular(int limit);
    Book addGenres(Long bookId, Set<Long> genreIds);
    Book removeGenres(Long bookId, Set<Long> genreIds);
    Book updateAvailableCopies(Long id, int availableCopies);
    Book updateTotalCopies(Long id, int totalCopies);
    Book setActive(Long id, boolean active);
    Book findById(Long id);
    Book update(Long id, BookDto bookDto);
    
    /**
     * Find books with low stock (below threshold).
     * 
     * @param threshold The minimum number of copies before considering stock low
     * @param pageable Pagination information
     * @return A page of books with low stock
     */
    Page<Book> findLowStockBooks(int threshold, Pageable pageable);

    /**
     * Check if a book needs restocking.
     * 
     * @param id The book ID
     * @param threshold The minimum number of copies before considering stock low
     * @return true if the book needs restocking, false otherwise
     */
    boolean needsRestocking(Long id, int threshold);

    long countTotalBooks();
}
