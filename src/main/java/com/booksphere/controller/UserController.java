package com.booksphere.controller;

import com.booksphere.dto.UserDto;
import com.booksphere.model.Notification;
import com.booksphere.model.Transaction;
import com.booksphere.model.User;
import com.booksphere.service.NotificationService;
import com.booksphere.service.TransactionService;
import com.booksphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for user operations.
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;

    /**
     * Display the user dashboard.
     * 
     * @param userDetails The authenticated user details
     * @param model The model
     * @return The user dashboard view
     */
    @GetMapping("/dashboard")
    public String showDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        
        // Get active rentals
        List<Transaction> activeRentals = transactionService.findActiveRentals(user);
        model.addAttribute("activeRentals", activeRentals);
        
        // Get unread notifications
        List<Notification> unreadNotifications = notificationService.findUnreadByUser(user);
        model.addAttribute("notifications", unreadNotifications);
        
        return "user/dashboard";
    }

    /**
     * Display the user profile page.
     * 
     * @param userDetails The authenticated user details
     * @param model The model
     * @return The user profile view
     */
    @GetMapping("/profile")
    public String showProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        
        if (!model.containsAttribute("userDto")) {
            UserDto userDto = new UserDto();
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setEmail(user.getEmail());
            userDto.setPhoneNumber(user.getPhoneNumber());
            userDto.setAddress(user.getAddress());
            
            model.addAttribute("userDto", userDto);
        }
        
        return "user/profile";
    }

    /**
     * Update user profile.
     * 
     * @param userDetails The authenticated user details
     * @param userDto The updated user information
     * @param bindingResult Validation results
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to profile page
     */
    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute("userDto") UserDto userDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDto", bindingResult);
            redirectAttributes.addFlashAttribute("userDto", userDto);
            return "redirect:/user/profile";
        }
        
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            userService.updateProfile(user.getId(), userDto);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("userDto", userDto);
        }
        
        return "redirect:/user/profile";
    }

    /**
     * Change user password.
     * 
     * @param userDetails The authenticated user details
     * @param oldPassword The old password
     * @param newPassword The new password
     * @param confirmPassword The confirmation of the new password
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to profile page
     */
    @PostMapping("/change-password")
    public String changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", "New password and confirmation do not match");
            return "redirect:/user/profile";
        }
        
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            userService.changePassword(user.getId(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("passwordSuccessMessage", "Password changed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", e.getMessage());
        }
        
        return "redirect:/user/profile";
    }

    /**
     * Display the user's transaction history.
     * 
     * @param userDetails The authenticated user details
     * @param page The page number
     * @param size The page size
     * @param model The model
     * @return The transaction history view
     */
    @GetMapping("/transactions")
    public String showTransactionHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        
        // Get transactions with pagination
        Page<Transaction> transactions = transactionService.findByUser(
                user, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        model.addAttribute("transactions", transactions);
        
        return "user/transaction-history";
    }

    /**
     * Display the notifications page.
     * 
     * @param userDetails The authenticated user details
     * @param page The page number
     * @param size The page size
     * @param model The model
     * @return The notifications view
     */
    @GetMapping("/notifications")
    public String showNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        
        // Get notifications with pagination
        Page<Notification> notifications = notificationService.findByUser(
                user, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        model.addAttribute("notifications", notifications);
        
        // Mark all as read
        notificationService.markAllAsRead(user.getId());
        
        return "user/notifications";
    }

    /**
     * Mark a notification as read.
     * 
     * @param id The notification ID
     * @return Redirect to notifications page
     */
    @PostMapping("/notifications/{id}/mark-read")
    public String markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/user/notifications";
    }
}
