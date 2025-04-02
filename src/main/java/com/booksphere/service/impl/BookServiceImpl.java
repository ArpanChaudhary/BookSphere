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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    @Transactional
    public Book createBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (book.getTotalCopies() < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        if (book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (book.getRentalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Rental price cannot be negative");
        }
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        if (bookDetails == null) {
            throw new IllegalArgumentException("Book details cannot be null");
        }
        if (bookDetails.getIsbn() == null || bookDetails.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
        if (bookDetails.getTitle() == null || bookDetails.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (bookDetails.getTotalCopies() < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        if (bookDetails.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (bookDetails.getRentalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Rental price cannot be negative");
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // Check if ISBN is changed and already exists
        if (!book.getIsbn().equals(bookDetails.getIsbn()) && 
                bookRepository.existsByIsbn(bookDetails.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN " + bookDetails.getIsbn() + " already exists");
        }

        book.setTitle(bookDetails.getTitle());
        book.setDescription(bookDetails.getDescription());
        book.setIsbn(bookDetails.getIsbn());
        book.setPrice(bookDetails.getPrice());
        book.setRentalPrice(bookDetails.getRentalPrice());
        book.setTotalCopies(bookDetails.getTotalCopies());
        book.setAvailableCopies(bookDetails.getAvailableCopies());
        book.setPublishedYear(bookDetails.getPublishedYear());
        book.setPublisher(bookDetails.getPublisher());
        book.setPublishedDate(bookDetails.getPublishedDate());
        book.setCoverImage(bookDetails.getCoverImage());
        book.setActive(bookDetails.isActive());

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        bookRepository.delete(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> searchBooks(String searchTerm, Pageable pageable) {
        return bookRepository.searchBooks(searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> getAvailableBooks(Pageable pageable) {
        return bookRepository.findAvailableBooks(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> getBooksByGenre(String genreName, Pageable pageable) {
        return bookRepository.findByGenreName(genreName, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> getBooksByYear(int year, Pageable pageable) {
        return bookRepository.findByPublishedYear(year, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> getOutOfStockBooks(Pageable pageable) {
        return bookRepository.findOutOfStockBooks(pageable);
    }

    @Override
    @Transactional
    public Book updateBookStock(Long id, int quantity) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        book.setTotalCopies(quantity);
        book.setAvailableCopies(Math.max(0, quantity - (book.getTotalCopies() - book.getAvailableCopies())));
        
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book create(BookDto bookDto, User author) {
        if (bookDto == null) {
            throw new IllegalArgumentException("Book DTO cannot be null");
        }
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setDescription(bookDto.getDescription());
        book.setIsbn(bookDto.getIsbn());
        book.setAuthor(author);
        book.setPrice(bookDto.getPrice());
        book.setRentalPrice(bookDto.getRentalPrice());
        book.setTotalCopies(bookDto.getTotalCopies());
        book.setAvailableCopies(bookDto.getTotalCopies());
        book.setPublishedYear(bookDto.getPublishedYear());
        book.setPublisher(bookDto.getPublisher());
        book.setPublishedDate(bookDto.getPublishedDate());
        book.setCoverImage(bookDto.getCoverImage());
        book.setActive(true);

        if (bookDto.getGenreIds() != null) {
            Set<Genre> genres = bookDto.getGenreIds().stream()
                    .map(genreId -> genreRepository.findById(genreId)
                            .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId)))
                    .collect(Collectors.toSet());
            book.setGenres(genres);
        }

        return bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findAvailable(Pageable pageable) {
        return bookRepository.findAvailableBooks(pageable);
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
    public List<Book> findMostPopular(int limit) {
        return bookRepository.findMostPopular(limit);
    }

    @Override
    @Transactional
    public Book addGenres(Long bookId, Set<Long> genreIds) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        Set<Genre> genres = genreIds.stream()
                .map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId)))
                .collect(Collectors.toSet());

        book.getGenres().addAll(genres);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book removeGenres(Long bookId, Set<Long> genreIds) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        Set<Genre> genresToRemove = genreIds.stream()
                .map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId)))
                .collect(Collectors.toSet());

        book.getGenres().removeAll(genresToRemove);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateAvailableCopies(Long id, int availableCopies) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

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
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }

        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(Math.max(0, totalCopies - (book.getTotalCopies() - book.getAvailableCopies())));
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book setActive(Long id, boolean active) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        book.setActive(active);
        return bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Override
    @Transactional
    public Book update(Long id, BookDto bookDto) {
        if (bookDto == null) {
            throw new IllegalArgumentException("Book DTO cannot be null");
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        book.setTitle(bookDto.getTitle());
        book.setDescription(bookDto.getDescription());
        book.setIsbn(bookDto.getIsbn());
        book.setPrice(bookDto.getPrice());
        book.setRentalPrice(bookDto.getRentalPrice());
        book.setTotalCopies(bookDto.getTotalCopies());
        book.setAvailableCopies(bookDto.getTotalCopies());
        book.setPublishedYear(bookDto.getPublishedYear());
        book.setPublisher(bookDto.getPublisher());
        book.setPublishedDate(bookDto.getPublishedDate());
        book.setCoverImage(bookDto.getCoverImage());

        if (bookDto.getGenreIds() != null) {
            Set<Genre> genres = bookDto.getGenreIds().stream()
                    .map(genreId -> genreRepository.findById(genreId)
                            .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId)))
                    .collect(Collectors.toSet());
            book.setGenres(genres);
        }

        return bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findLowStockBooks(int threshold, Pageable pageable) {
        return bookRepository.findByAvailableCopiesLessThan(threshold, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean needsRestocking(Long id, int threshold) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return book.getAvailableCopies() < threshold;
    }

    @Override
    public long countTotalBooks() {
        return bookRepository.count();
    }
}
