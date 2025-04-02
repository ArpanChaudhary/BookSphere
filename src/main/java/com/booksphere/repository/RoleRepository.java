package com.booksphere.repository;

import com.booksphere.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find a role by its name.
     * 
     * @param name The role name
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}