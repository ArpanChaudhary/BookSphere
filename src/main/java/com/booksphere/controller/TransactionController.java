package com.booksphere.controller;

import com.booksphere.model.Transaction;
import com.booksphere.model.User;
import com.booksphere.service.TransactionService;
import com.booksphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for transaction operations.
 */
@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    /**
     * Return a book.
     * 
     * @param userDetails The authenticated user details
     * @param id The transaction ID
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to user's transactions page
     */
    @PostMapping("/{id}/return")
    public String returnBook(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Return the book
            Transaction transaction = transactionService.returnBook(id);
            
            // Calculate late fee if any
            if (transaction.isOverdue()) {
                transactionService.calculateLateFee(id);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/user/transactions";
    }

    /**
     * View transaction details.
     * 
     * @param id The transaction ID
     * @param model The model
     * @return The transaction details view
     */
    @GetMapping("/{id}")
    public String viewTransaction(@PathVariable Long id, Model model) {
        Transaction transaction = transactionService.findById(id);
        model.addAttribute("transaction", transaction);
        
        return "user/transaction-details";
    }

    /**
     * Pay transaction fees.
     * 
     * @param id The transaction ID
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to transaction details page
     */
    @PostMapping("/{id}/pay")
    public String payFees(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            transactionService.payFees(id);
            redirectAttributes.addFlashAttribute("successMessage", "Payment processed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/transactions/" + id;
    }

    /**
     * Issue a book to a user (admin function).
     * 
     * @param userId The user ID
     * @param bookId The book ID
     * @param days The rental period in days
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to admin's transaction management page
     */
    @PostMapping("/issue")
    public String issueBook(
            @RequestParam Long userId,
            @RequestParam Long bookId,
            @RequestParam(defaultValue = "14") int days,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Calculate due date
            java.time.LocalDateTime dueDate = java.time.LocalDateTime.now().plusDays(days);
            
            // Issue the book
            transactionService.issueBook(userId, bookId, dueDate);
            
            redirectAttributes.addFlashAttribute("successMessage", "Book issued successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/admin/transactions";
    }
}
