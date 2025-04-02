package com.booksphere.repository;

import com.booksphere.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Genre entity.
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Find a genre by its name.
     * 
     * @param name The name of the genre to find
     * @return Optional containing the genre if found
     */
    Optional<Genre> findByName(String name);

    /**
     * Check if a genre exists by its name.
     * 
     * @param name The name of the genre to check
     * @return true if the genre exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Find genres by name containing a search term (case insensitive).
     * 
     * @param name The name search term
     * @return A list of genres matching the name
     */
    List<Genre> findByNameContainingIgnoreCase(String name);

    /**
     * Find the most popular genres (based on number of books).
     * 
     * @param limit The maximum number of genres to return
     * @return A list of the most popular genres
     */
    @Query(value = 
           "SELECT g.* FROM genres g " +
           "JOIN book_genres bg ON g.id = bg.genre_id " +
           "GROUP BY g.id " +
           "ORDER BY COUNT(bg.book_id) DESC " +
           "LIMIT :limit", 
           nativeQuery = true)
    List<Genre> findMostPopularGenres(int limit);
}
