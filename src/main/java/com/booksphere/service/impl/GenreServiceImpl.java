package com.booksphere.service.impl;

import com.booksphere.model.Genre;
import com.booksphere.repository.GenreRepository;
import com.booksphere.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of GenreService interface.
 */
@Service
public class GenreServiceImpl implements GenreService {

    @Autowired
    private GenreRepository genreRepository;

    @Override
    @Transactional
    public Genre createGenre(Genre genre) {
        if (genreRepository.existsByName(genre.getName())) {
            throw new IllegalArgumentException("Genre with name '" + genre.getName() + "' already exists");
        }
        return genreRepository.save(genre);
    }

    @Override
    @Transactional
    public Genre updateGenre(Long id, Genre genre) {
        Genre existingGenre = getGenreById(id);
        existingGenre.setName(genre.getName());
        existingGenre.setDescription(genre.getDescription());
        existingGenre.setActive(genre.isActive());
        return genreRepository.save(existingGenre);
    }

    @Override
    @Transactional
    public void deleteGenre(Long id) {
        Genre genre = getGenreById(id);
        genreRepository.delete(genre);
    }

    @Override
    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Genre not found with id: " + id));
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Page<Genre> getGenres(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    @Override
    public Genre getGenreByName(String name) {
        return genreRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Genre not found with name: " + name));
    }
} 