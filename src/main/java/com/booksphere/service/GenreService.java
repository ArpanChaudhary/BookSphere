package com.booksphere.service;

import com.booksphere.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing book genres.
 */
public interface GenreService {
    
    /**
     * Create a new genre.
     * 
     * @param genre The genre to create
     * @return The created genre
     */
    Genre createGenre(Genre genre);
    
    /**
     * Update an existing genre.
     * 
     * @param id The ID of the genre to update
     * @param genre The updated genre data
     * @return The updated genre
     */
    Genre updateGenre(Long id, Genre genre);
    
    /**
     * Delete a genre by its ID.
     * 
     * @param id The ID of the genre to delete
     */
    void deleteGenre(Long id);
    
    /**
     * Get a genre by its ID.
     * 
     * @param id The ID of the genre to retrieve
     * @return The genre
     */
    Genre getGenreById(Long id);
    
    /**
     * Get all genres.
     * 
     * @return List of all genres
     */
    List<Genre> getAllGenres();
    
    /**
     * Get all genres without pagination.
     * 
     * @return List of all genres
     */
    List<Genre> findAll();
    
    /**
     * Get genres with pagination.
     * 
     * @param pageable The pagination information
     * @return Page of genres
     */
    Page<Genre> getGenres(Pageable pageable);
    
    /**
     * Get a genre by its name.
     * 
     * @param name The name of the genre to retrieve
     * @return The genre
     */
    Genre getGenreByName(String name);
} 