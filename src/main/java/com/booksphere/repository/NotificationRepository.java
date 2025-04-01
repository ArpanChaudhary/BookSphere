package com.booksphere.repository;

import com.booksphere.model.Notification;
import com.booksphere.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for Notification entity.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications for a user.
     * 
     * @param user The user
     * @param pageable Pagination information
     * @return A page of notifications for the user
     */
    Page<Notification> findByUser(User user, Pageable pageable);

    /**
     * Find unread notifications for a user.
     * 
     * @param user The user
     * @return A list of unread notifications
     */
    List<Notification> findByUserAndIsReadFalse(User user);

    /**
     * Count unread notifications for a user.
     * 
     * @param user The user
     * @return The number of unread notifications
     */
    long countByUserAndIsReadFalse(User user);

    /**
     * Find notifications by type for a user.
     * 
     * @param user The user
     * @param type The notification type
     * @param pageable Pagination information
     * @return A page of notifications of the specified type
     */
    Page<Notification> findByUserAndType(User user, Notification.NotificationType type, Pageable pageable);

    /**
     * Mark all notifications as read for a user.
     * 
     * @param userId The user ID
     * @return The number of updated notifications
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);
}
