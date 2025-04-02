package com.booksphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import com.booksphere.model.Notification;

/**
 * Data Transfer Object for notification information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Notification type is required")
    private Notification.NotificationType type;

    private boolean isRead = false;

    private LocalDateTime createdAt;

    private Long bookId;
} 