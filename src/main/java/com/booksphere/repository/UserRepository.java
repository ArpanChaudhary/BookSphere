package com.booksphere.repository;

import com.booksphere.model.User;
import com.booksphere.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their username.
     * 
     * @param username The username to search for
     * @return An optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by their email address.
     * 
     * @param email The email to search for
     * @return An optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a username exists.
     * 
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email exists.
     * 
     * @param email The email to check
     * @return true if the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role.
     * 
     * @param role The role to search for
     * @return A list of users with the specified role
     */
    @Query("SELECT u FROM User u WHERE u.userRole = :role")
    List<User> findByRole(@Param("role") UserRole role);

    /**
     * Find users by role name.
     * 
     * @param roleName The role name to search for
     * @return A list of users with the specified role name
     */
    @Query("SELECT u FROM User u WHERE u.userRole.name() = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    /**
     * Search users by first name, last name, or email.
     * 
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return A page of matching users
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find users by role name with pagination.
     * 
     * @param name The role name
     * @param pageable The pagination information
     * @return Page of users with the specified role
     */
    Page<User> findByRoles_Name(String name, Pageable pageable);

    /**
     * Find users by enabled status with pagination.
     * 
     * @param enabled The enabled status
     * @param pageable The pagination information
     * @return Page of users with the specified enabled status
     */
    Page<User> findByEnabled(boolean enabled, Pageable pageable);

    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, Pageable pageable);
}
