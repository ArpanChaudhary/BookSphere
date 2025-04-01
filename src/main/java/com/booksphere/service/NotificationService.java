package com.booksphere.service;

import com.booksphere.model.Notification;
import com.booksphere.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing notifications.
 */
public interface NotificationService {

    /**
     * Find a notification by ID.
     * 
     * @param id The notification ID
     * @return The notification
     */
    Notification findById(Long id);

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
    List<Notification> findUnreadByUser(User user);

    /**
     * Count unread notifications for a user.
     * 
     * @param user The user
     * @return The number of unread notifications
     */
    long countUnreadByUser(User user);

    /**
     * Mark a notification as read.
     * 
     * @param id The notification ID
     * @return The updated notification
     */
    Notification markAsRead(Long id);

    /**
     * Mark all notifications as read for a user.
     * 
     * @param userId The user ID
     * @return The number of updated notifications
     */
    int markAllAsRead(Long userId);

    /**
     * Create a new notification.
     * 
     * @param notification The notification to create
     * @return The created notification
     */
    Notification create(Notification notification);

    /**
     * Delete a notification.
     * 
     * @param id The notification ID
     */
    void delete(Long id);
    
    /**
     * Create a notification for a user with a message and type.
     * 
     * @param userId The user ID
     * @param message The notification message
     * @param type The notification type
     * @return The created notification
     */
    Notification createForUser(Long userId, String message, Notification.NotificationType type);
    
    /**
     * Create a notification for all users with a specific role.
     * 
     * @param roleName The role name
     * @param message The notification message
     * @param type The notification type
     * @return The number of created notifications
     */
    int createForRole(String roleName, String message, Notification.NotificationType type);
    
    /**
     * Create notifications for overdue books.
     * 
     * @return The number of created notifications
     */
    int createOverdueNotifications();
}
