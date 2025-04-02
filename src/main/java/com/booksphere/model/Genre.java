package com.booksphere.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a book genre in the BookSphere system.
 */
@Entity
@Table(name = "genres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "genres")
    private Set<Book> books = new HashSet<>();

    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * Constructor with genre name
     * 
     * @param name The name of the genre
     */
    public Genre(String name) {
        this.name = name;
    }

    /**
     * Constructor with genre name and description
     * 
     * @param name The name of the genre
     * @param description The description of the genre
     */
    public Genre(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
