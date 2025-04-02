package com.booksphere.repository;

import com.booksphere.model.Book;
import com.booksphere.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book entity.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Find a book by its ISBN.
     * 
     * @param isbn The ISBN to search for
     * @return An optional containing the book if found
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Check if a book exists by ISBN.
     * 
     * @param isbn The ISBN to check
     * @return true if a book with the given ISBN exists, false otherwise
     */
    boolean existsByIsbn(String isbn);

    /**
     * Find all available books.
     * 
     * @param pageable Pagination information
     * @return A page of available books
     */
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 AND b.active = true")
    Page<Book> findAvailableBooks(Pageable pageable);

    /**
     * Search books by title, description, or ISBN.
     * 
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return A page of matching books
     */
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "b.isbn LIKE CONCAT('%', :searchTerm, '%')")
    Page<Book> searchBooks(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find books by author.
     * 
     * @param author The author
     * @param pageable Pagination information
     * @return A page of books by the author
     */
    Page<Book> findByAuthor(User author, Pageable pageable);

    /**
     * Find books by genre ID.
     * 
     * @param genreId The genre ID
     * @param pageable Pagination information
     * @return A page of books in the genre
     */
    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g.id = :genreId")
    Page<Book> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);

    /**
     * Find the most popular books based on number of transactions.
     * 
     * @param limit The maximum number of books to return
     * @return A list of the most popular books
     */
    @Query("SELECT b FROM Book b ORDER BY SIZE(b.transactions) DESC")
    List<Book> findMostPopular(int limit);

    /**
     * Find books by author ID.
     * 
     * @param authorId The author ID
     * @return A list of books by the author
     */
    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") Long authorId);

    /**
     * Find books by genre name.
     * 
     * @param genreName The genre name
     * @param pageable Pagination information
     * @return A page of books in the genre
     */
    @Query("SELECT b FROM Book b JOIN b.genres g WHERE LOWER(g.name) = LOWER(:genreName)")
    Page<Book> findByGenreName(@Param("genreName") String genreName, Pageable pageable);

    /**
     * Find books by publication year.
     * 
     * @param year The publication year
     * @param pageable Pagination information
     * @return A page of books published in the given year
     */
    @Query("SELECT b FROM Book b WHERE b.publishedYear = :year")
    Page<Book> findByPublishedYear(@Param("year") int year, Pageable pageable);

    /**
     * Find out of stock books.
     * 
     * @param pageable Pagination information
     * @return A page of out of stock books
     */
    @Query("SELECT b FROM Book b WHERE b.availableCopies = 0")
    Page<Book> findOutOfStockBooks(Pageable pageable);

    /**
     * Find books that belong to any of the specified genres.
     * 
     * @param genreIds The list of genre IDs
     * @param pageable Pagination information
     * @return A page of books in any of the specified genres
     */
    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.id IN :genreIds AND b.active = true")
    Page<Book> findByGenres(@Param("genreIds") List<Long> genreIds, Pageable pageable);

    /**
     * Find books within a specified price range.
     * 
     * @param minPrice The minimum price
     * @param maxPrice The maximum price
     * @param pageable Pagination information
     * @return A page of books within the price range
     */
    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :minPrice AND :maxPrice AND b.active = true")
    Page<Book> findByPriceRange(@Param("minPrice") double minPrice, 
                              @Param("maxPrice") double maxPrice, 
                              Pageable pageable);

    /**
     * Find books by availability status.
     * 
     * @param available Whether to find available or unavailable books
     * @param pageable Pagination information
     * @return A page of books with the specified availability status
     */
    @Query("SELECT b FROM Book b WHERE (:available = true AND b.availableCopies > 0) OR (:available = false AND b.availableCopies = 0) AND b.active = true")
    Page<Book> findByAvailability(@Param("available") boolean available, Pageable pageable);

    /**
     * Find books with available copies less than the specified threshold.
     * 
     * @param threshold The minimum number of copies
     * @param pageable Pagination information
     * @return A page of books with low stock
     */
    @Query("SELECT b FROM Book b WHERE b.availableCopies < :threshold AND b.active = true")
    Page<Book> findByAvailableCopiesLessThan(@Param("threshold") int threshold, Pageable pageable);
}
