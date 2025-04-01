package com.booksphere.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a book in the BookSphere system.
 */
@Entity
@Table(name = "books")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private int publicationYear;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private int totalCopies;

    @Column(nullable = false)
    private int availableCopies;

    @Column(nullable = false)
    private BigDecimal rentalPrice;
    
    @Column(name = "published_date")
    private LocalDate publishedDate;
    
    @Column(name = "price")
    private BigDecimal price;
    
    @Column(name = "cover_image")
    private String coverImage;
    
    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToMany
    @JoinTable(
        name = "book_genres",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "book")
    private Set<Transaction> transactions = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean active = true;

    /**
     * Checks if the book is available for rental.
     * 
     * @return true if there are available copies, false otherwise
     */
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /**
     * Method to add a genre to this book.
     * 
     * @param genre The genre to add
     */
    public void addGenre(Genre genre) {
        this.genres.add(genre);
    }

    /**
     * Method to check out a copy of this book.
     * 
     * @return true if checkout was successful, false if no copies available
     */
    public boolean checkoutCopy() {
        if (availableCopies > 0) {
            availableCopies--;
            return true;
        }
        return false;
    }

    /**
     * Method to return a copy of this book.
     */
    public void returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }
}
