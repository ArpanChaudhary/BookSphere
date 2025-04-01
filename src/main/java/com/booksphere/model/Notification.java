package com.booksphere.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing a notification in the BookSphere system.
 */
@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private boolean isRead = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    /**
     * Enum representing the type of notification.
     */
    public enum NotificationType {
        DUE_REMINDER,
        OVERDUE_ALERT,
        BOOK_AVAILABLE,
        NEW_BOOK_ADDED,
        AUTHOR_UPDATE,
        SYSTEM_NOTIFICATION,
        SYSTEM
    }

    /**
     * Constructor for creating a new notification.
     * 
     * @param user The user to notify
     * @param message The notification message
     * @param type The type of notification
     */
    public Notification(User user, String message, NotificationType type) {
        this.user = user;
        this.message = message;
        this.type = type;
    }

    /**
     * Constructor for creating a book-related notification.
     * 
     * @param user The user to notify
     * @param message The notification message
     * @param type The type of notification
     * @param book The related book
     */
    public Notification(User user, String message, NotificationType type, Book book) {
        this.user = user;
        this.message = message;
        this.type = type;
        this.book = book;
    }
}
