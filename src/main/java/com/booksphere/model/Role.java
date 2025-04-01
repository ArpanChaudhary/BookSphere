package com.booksphere.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a user role in the BookSphere system.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "users")
@EqualsAndHashCode(exclude = "users")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    /**
     * Constructor with role name
     * 
     * @param name The name of the role
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Constructor with role name and description
     * 
     * @param name The name of the role
     * @param description The description of the role
     */
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
