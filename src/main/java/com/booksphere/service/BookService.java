package com.booksphere.service;

import com.booksphere.dto.BookDto;
import com.booksphere.model.Book;
import com.booksphere.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * Service interface for managing books.
 */
public interface BookService {

    /**
     * Find a book by ID.
     * 
     * @param id The book ID
     * @return The book
     */
    Book findById(Long id);

    /**
     * Find a book by ISBN.
     * 
     * @param isbn The ISBN
     * @return The book
     */
    Book findByIsbn(String isbn);

    /**
     * Find all books with pagination.
     * 
     * @param pageable Pagination information
     * @return A page of books
     */
    Page<Book> findAll(Pageable pageable);

    /**
     * Find all available books (with at least one copy available).
     * 
     * @param pageable Pagination information
     * @return A page of available books
     */
    Page<Book> findAvailable(Pageable pageable);

    /**
     * Create a new book.
     * 
     * @param bookDto The book information
     * @param author The author user
     * @return The created book
     */
    Book create(BookDto bookDto, User author);

    /**
     * Update a book.
     * 
     * @param id The book ID
     * @param bookDto The updated book information
     * @return The updated book
     */
    Book update(Long id, BookDto bookDto);

    /**
     * Delete a book.
     * 
     * @param id The book ID
     */
    void delete(Long id);

    /**
     * Search for books.
     * 
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return A page of matching books
     */
    Page<Book> searchBooks(String searchTerm, Pageable pageable);

    /**
     * Find books by author.
     * 
     * @param author The author user
     * @param pageable Pagination information
     * @return A page of books by the author
     */
    Page<Book> findByAuthor(User author, Pageable pageable);

    /**
     * Find books by genre.
     * 
     * @param genreId The genre ID
     * @param pageable Pagination information
     * @return A page of books in the genre
     */
    Page<Book> findByGenre(Long genreId, Pageable pageable);

    /**
     * Find the most popular books (most rented).
     * 
     * @param limit The maximum number of books to return
     * @return A list of popular books
     */
    List<Book> findMostPopular(int limit);

    /**
     * Add genres to a book.
     * 
     * @param bookId The book ID
     * @param genreIds The genre IDs to add
     * @return The updated book
     */
    Book addGenres(Long bookId, Set<Long> genreIds);

    /**
     * Remove genres from a book.
     * 
     * @param bookId The book ID
     * @param genreIds The genre IDs to remove
     * @return The updated book
     */
    Book removeGenres(Long bookId, Set<Long> genreIds);

    /**
     * Update the available copies of a book.
     * 
     * @param id The book ID
     * @param availableCopies The new number of available copies
     * @return The updated book
     */
    Book updateAvailableCopies(Long id, int availableCopies);

    /**
     * Update the total copies of a book.
     * 
     * @param id The book ID
     * @param totalCopies The new total number of copies
     * @return The updated book
     */
    Book updateTotalCopies(Long id, int totalCopies);
    
    /**
     * Activate or deactivate a book.
     * 
     * @param id The book ID
     * @param active The active status
     * @return The updated book
     */
    Book setActive(Long id, boolean active);
}
