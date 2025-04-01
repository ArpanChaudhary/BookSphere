package com.booksphere.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a book genre in the BookSphere system.
 */
@Entity
@Table(name = "genres")
@Data
@NoArgsConstructor
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @ManyToMany(mappedBy = "genres")
    private Set<Book> books = new HashSet<>();

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
