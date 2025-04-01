package com.booksphere.service.impl;

import com.booksphere.dto.BookDto;
import com.booksphere.exception.ResourceNotFoundException;
import com.booksphere.model.Book;
import com.booksphere.model.Genre;
import com.booksphere.model.User;
import com.booksphere.repository.BookRepository;
import com.booksphere.repository.GenreRepository;
import com.booksphere.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the BookService interface.
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findAvailable(Pageable pageable) {
        return bookRepository.findByActiveIsTrueAndAvailableCopiesGreaterThan(0, pageable);
    }

    @Override
    @Transactional
    public Book create(BookDto bookDto, User author) {
        // Check if ISBN already exists
        if (bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Book with ISBN " + bookDto.getIsbn() + " already exists");
        }

        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setDescription(bookDto.getDescription());
        book.setIsbn(bookDto.getIsbn());
        book.setPublicationYear(bookDto.getPublicationYear());
        book.setPublisher(bookDto.getPublisher());
        book.setTotalCopies(bookDto.getTotalCopies());
        book.setAvailableCopies(bookDto.getTotalCopies()); // Initially, all copies are available
        book.setRentalPrice(bookDto.getRentalPrice());
        book.setAuthor(author);
        book.setActive(true);

        // Add genres if provided
        if (bookDto.getGenreIds() != null && !bookDto.getGenreIds().isEmpty()) {
            Set<Genre> genres = bookDto.getGenreIds().stream()
                    .map(genreId -> genreRepository.findById(genreId)
                            .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId)))
                    .collect(Collectors.toSet());
            book.setGenres(genres);
        }

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book update(Long id, BookDto bookDto) {
        Book book = findById(id);

        // Check if ISBN is changed and already exists
        if (!book.getIsbn().equals(bookDto.getIsbn()) && 
                bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Book with ISBN " + bookDto.getIsbn() + " already exists");
        }

        book.setTitle(bookDto.getTitle());
        book.setDescription(bookDto.getDescription());
        book.setIsbn(bookDto.getIsbn());
        book.setPublicationYear(bookDto.getPublicationYear());
        book.setPublisher(bookDto.getPublisher());
        book.setRentalPrice(bookDto.getRentalPrice());

        // Add genres if provided
        if (bookDto.getGenreIds() != null && !bookDto.getGenreIds().isEmpty()) {
            Set<Genre> genres = bookDto.getGenreIds().stream()
                    .map(genreId -> genreRepository.findById(genreId)
                            .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId)))
                    .collect(Collectors.toSet());
            book.setGenres(genres);
        }

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = findById(id);
        
        // Instead of deleting, deactivate the book
        book.setActive(false);
        bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> searchBooks(String searchTerm, Pageable pageable) {
        return bookRepository.searchBooks(searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findByAuthor(User author, Pageable pageable) {
        return bookRepository.findByAuthor(author, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findByGenre(Long genreId, Pageable pageable) {
        return bookRepository.findByGenreId(genreId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findMostPopular(int limit) {
        return bookRepository.findMostRentedBooks(limit);
    }

    @Override
    @Transactional
    public Book addGenres(Long bookId, Set<Long> genreIds) {
        Book book = findById(bookId);
        
        Set<Genre> genresToAdd = genreIds.stream()
                .map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId)))
                .collect(Collectors.toSet());
        
        book.getGenres().addAll(genresToAdd);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book removeGenres(Long bookId, Set<Long> genreIds) {
        Book book = findById(bookId);
        
        book.setGenres(book.getGenres().stream()
                .filter(genre -> !genreIds.contains(genre.getId()))
                .collect(Collectors.toSet()));
        
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateAvailableCopies(Long id, int availableCopies) {
        Book book = findById(id);
        
        if (availableCopies < 0) {
            throw new IllegalArgumentException("Available copies cannot be negative");
        }
        
        if (availableCopies > book.getTotalCopies()) {
            throw new IllegalArgumentException("Available copies cannot exceed total copies");
        }
        
        book.setAvailableCopies(availableCopies);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateTotalCopies(Long id, int totalCopies) {
        Book book = findById(id);
        
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        
        if (totalCopies < book.getAvailableCopies()) {
            book.setAvailableCopies(totalCopies);
        }
        
        book.setTotalCopies(totalCopies);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book setActive(Long id, boolean active) {
        Book book = findById(id);
        book.setActive(active);
        return bookRepository.save(book);
    }
}
