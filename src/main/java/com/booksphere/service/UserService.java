package com.booksphere.service;

import com.booksphere.dto.UserDto;
import com.booksphere.dto.RegistrationRequest;
import com.booksphere.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing users.
 */
public interface UserService extends UserDetailsService {
    User createUser(User user);
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    User updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    void toggleUserStatus(Long id);
    User register(RegistrationRequest registrationRequest);
    User findById(Long id);
    User findByUsername(String username);
    User findByEmail(String email);
    User updateProfile(Long id, UserDto userDto);
    boolean changePassword(Long id, String oldPassword, String newPassword);
    Page<User> findAll(Pageable pageable);
    Page<User> searchUsers(String searchTerm, Pageable pageable);
    List<User> findByRole(String roleName);
    User addRole(Long userId, String roleName);
    User removeRole(Long userId, String roleName);
    User setEnabled(Long id, boolean enabled);
    long countTotalUsers();
    Page<User> findByRole(String role, Pageable pageable);
    Page<User> findByEnabled(boolean enabled, Pageable pageable);
    User registerUser(UserDto userDto);
    Page<User> getAllUsers(Pageable pageable);
}
