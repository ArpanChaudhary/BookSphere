package com.booksphere.service;

import com.booksphere.dto.UserDto;
import com.booksphere.dto.RegistrationRequest;
import com.booksphere.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Service interface for managing users.
 */
public interface UserService extends UserDetailsService {

    /**
     * Register a new user.
     * 
     * @param registrationRequest The registration request
     * @return The newly created user
     */
    User register(RegistrationRequest registrationRequest);

    /**
     * Find a user by ID.
     * 
     * @param id The user ID
     * @return The user
     */
    User findById(Long id);

    /**
     * Find a user by username.
     * 
     * @param username The username
     * @return The user
     */
    User findByUsername(String username);

    /**
     * Find a user by email.
     * 
     * @param email The email
     * @return The user
     */
    User findByEmail(String email);

    /**
     * Update user profile information.
     * 
     * @param id The user ID
     * @param userDto The updated user information
     * @return The updated user
     */
    User updateProfile(Long id, UserDto userDto);

    /**
     * Change a user's password.
     * 
     * @param id The user ID
     * @param oldPassword The old password
     * @param newPassword The new password
     * @return True if the password was changed successfully
     */
    boolean changePassword(Long id, String oldPassword, String newPassword);

    /**
     * Find all users with pagination.
     * 
     * @param pageable Pagination information
     * @return A page of users
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Delete a user.
     * 
     * @param id The user ID
     */
    void delete(Long id);

    /**
     * Search for users.
     * 
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return A page of matching users
     */
    Page<User> searchUsers(String searchTerm, Pageable pageable);

    /**
     * Find users by role.
     * 
     * @param roleName The role name
     * @return A list of users with the role
     */
    List<User> findByRole(String roleName);

    /**
     * Add a role to a user.
     * 
     * @param userId The user ID
     * @param roleName The role name
     * @return The updated user
     */
    User addRole(Long userId, String roleName);

    /**
     * Remove a role from a user.
     * 
     * @param userId The user ID
     * @param roleName The role name
     * @return The updated user
     */
    User removeRole(Long userId, String roleName);

    /**
     * Enable or disable a user account.
     * 
     * @param id The user ID
     * @param enabled The enabled status
     * @return The updated user
     */
    User setEnabled(Long id, boolean enabled);
}
