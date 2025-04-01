package com.booksphere.service.impl;

import com.booksphere.exception.ResourceNotFoundException;
import com.booksphere.model.Notification;
import com.booksphere.model.Transaction;
import com.booksphere.model.User;
import com.booksphere.repository.NotificationRepository;
import com.booksphere.repository.TransactionRepository;
import com.booksphere.repository.UserRepository;
import com.booksphere.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the NotificationService interface.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> findByUser(User user, Pageable pageable) {
        return notificationRepository.findByUser(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findUnreadByUser(User user) {
        return notificationRepository.findByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadByUser(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public Notification markAsRead(Long id) {
        Notification notification = findById(id);
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public Notification create(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Notification notification = findById(id);
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public Notification createForUser(Long userId, String message, Notification.NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public int createForRole(String roleName, String message, Notification.NotificationType type) {
        List<User> users = userRepository.findByRoleName(roleName);
        
        for (User user : users) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setMessage(message);
            notification.setType(type);
            
            notificationRepository.save(notification);
        }
        
        return users.size();
    }

    @Override
    @Transactional
    public int createOverdueNotifications() {
        List<Transaction> overdueTransactions = transactionRepository.findOverdueTransactions(LocalDateTime.now());
        int count = 0;
        
        for (Transaction transaction : overdueTransactions) {
            // Don't create duplicate notifications - check if a notification already exists
            List<Notification> existingNotifications = notificationRepository.findByUserAndIsReadFalse(transaction.getUser());
            boolean alreadyNotified = existingNotifications.stream()
                    .anyMatch(n -> n.getType() == Notification.NotificationType.OVERDUE_ALERT && 
                                 n.getBook() != null && 
                                 n.getBook().getId().equals(transaction.getBook().getId()));
                                 
            if (!alreadyNotified) {
                Notification notification = new Notification();
                notification.setUser(transaction.getUser());
                notification.setMessage("Your book '" + transaction.getBook().getTitle() + "' is overdue. " +
                                      "Please return it as soon as possible to avoid additional late fees.");
                notification.setType(Notification.NotificationType.OVERDUE_ALERT);
                notification.setBook(transaction.getBook());
                
                notificationRepository.save(notification);
                count++;
            }
        }
        
        return count;
    }
}
