package com.booksphere.repository;

import com.booksphere.model.Book;
import com.booksphere.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book entity.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Find books by title containing a search term (case insensitive).
     * 
     * @param title The title search term
     * @param pageable Pagination information
     * @return A page of books matching the title
     */
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Find books by ISBN.
     * 
     * @param isbn The ISBN to search for
     * @return An optional containing the book if found
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Find books by author.
     * 
     * @param author The author user
     * @param pageable Pagination information
     * @return A page of books by the author
     */
    Page<Book> findByAuthor(User author, Pageable pageable);

    /**
     * Find books by publication year.
     * 
     * @param year The publication year
     * @param pageable Pagination information
     * @return A page of books published in the given year
     */
    Page<Book> findByPublicationYear(int year, Pageable pageable);

    /**
     * Find active books with available copies.
     * 
     * @param pageable Pagination information
     * @return A page of available books
     */
    Page<Book> findByActiveIsTrueAndAvailableCopiesGreaterThan(int minAvailableCopies, Pageable pageable);

    /**
     * Search books by title, description, author name, or ISBN.
     * 
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return A page of matching books
     */
    @Query("SELECT b FROM Book b LEFT JOIN b.author a WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Book> searchBooks(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find books by genre.
     * 
     * @param genreId The genre ID
     * @param pageable Pagination information
     * @return A page of books in the given genre
     */
    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g.id = :genreId")
    Page<Book> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);

    /**
     * Find the most rented books.
     * 
     * @param limit The maximum number of books to return
     * @return A list of the most rented books
     */
    @Query(value = 
           "SELECT b.* FROM books b " +
           "JOIN transactions t ON b.id = t.book_id " +
           "GROUP BY b.id " +
           "ORDER BY COUNT(t.id) DESC " +
           "LIMIT :limit", 
           nativeQuery = true)
    List<Book> findMostRentedBooks(@Param("limit") int limit);
}
