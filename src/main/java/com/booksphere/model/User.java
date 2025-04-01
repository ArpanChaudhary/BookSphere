package com.booksphere.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a user in the BookSphere system.
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String phoneNumber;

    @Column
    private String address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column
    private boolean active = true;
    
    @Column
    private boolean enabled = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Transaction> transactions = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private Set<Book> authoredBooks = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Notification> notifications = new HashSet<>();

    /**
     * Convenience method to add a role to this user.
     * 
     * @param role The role to add
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Returns the full name of the user.
     * 
     * @return The full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
