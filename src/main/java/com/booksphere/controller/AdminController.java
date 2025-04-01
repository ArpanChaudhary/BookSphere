package com.booksphere.controller;

import com.booksphere.dto.UserDto;
import com.booksphere.model.Book;
import com.booksphere.model.Transaction;
import com.booksphere.model.User;
import com.booksphere.service.BookService;
import com.booksphere.service.ReportService;
import com.booksphere.service.TransactionService;
import com.booksphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller for admin operations.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BookService bookService;
    private final TransactionService transactionService;
    private final ReportService reportService;

    /**
     * Display the admin dashboard.
     * 
     * @param model The model
     * @return The admin dashboard view
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Get counts for dashboard
        long userCount = userService.findAll(PageRequest.of(0, 1)).getTotalElements();
        long bookCount = bookService.findAll(PageRequest.of(0, 1)).getTotalElements();
        long transactionCount = transactionService.findAll(PageRequest.of(0, 1)).getTotalElements();
        
        // Get overdue books
        List<Transaction> overdueTransactions = transactionService.findOverdueTransactions();
        
        // Get most popular books
        List<Book> popularBooks = bookService.findMostPopular(5);
        
        model.addAttribute("userCount", userCount);
        model.addAttribute("bookCount", bookCount);
        model.addAttribute("transactionCount", transactionCount);
        model.addAttribute("overdueCount", overdueTransactions.size());
        model.addAttribute("overdueTransactions", overdueTransactions);
        model.addAttribute("popularBooks", popularBooks);
        
        return "admin/dashboard";
    }

    /**
     * Display the manage users page.
     * 
     * @param page The page number
     * @param size The page size
     * @param search The search term
     * @param model The model
     * @return The manage users view
     */
    @GetMapping("/users")
    public String manageUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {
        
        Page<User> users;
        
        if (search != null && !search.isEmpty()) {
            users = userService.searchUsers(search, PageRequest.of(page, size, Sort.by("id").ascending()));
            model.addAttribute("search", search);
        } else {
            users = userService.findAll(PageRequest.of(page, size, Sort.by("id").ascending()));
        }
        
        model.addAttribute("users", users);
        
        return "admin/manage-users";
    }

    /**
     * Display the edit user page.
     * 
     * @param id The user ID
     * @param model The model
     * @return The edit user view
     */
    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setAddress(user.getAddress());
        
        model.addAttribute("user", user);
        model.addAttribute("userDto", userDto);
        
        return "admin/edit-user";
    }

    /**
     * Update a user.
     * 
     * @param id The user ID
     * @param userDto The updated user information
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to manage users page
     */
    @PostMapping("/users/{id}")
    public String updateUser(
            @PathVariable Long id,
            @ModelAttribute UserDto userDto,
            RedirectAttributes redirectAttributes) {
        
        try {
            userService.updateProfile(id, userDto);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    /**
     * Enable or disable a user.
     * 
     * @param id The user ID
     * @param enabled The enabled status
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to manage users page
     */
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled,
            RedirectAttributes redirectAttributes) {
        
        try {
            userService.setEnabled(id, enabled);
            String status = enabled ? "enabled" : "disabled";
            redirectAttributes.addFlashAttribute("successMessage", "User " + status + " successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    /**
     * Add a role to a user.
     * 
     * @param id The user ID
     * @param role The role name
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to edit user page
     */
    @PostMapping("/users/{id}/add-role")
    public String addRole(
            @PathVariable Long id,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        
        try {
            userService.addRole(id, role);
            redirectAttributes.addFlashAttribute("successMessage", "Role added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/admin/users/" + id + "/edit";
    }

    /**
     * Remove a role from a user.
     * 
     * @param id The user ID
     * @param role The role name
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to edit user page
     */
    @PostMapping("/users/{id}/remove-role")
    public String removeRole(
            @PathVariable Long id,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        
        try {
            userService.removeRole(id, role);
            redirectAttributes.addFlashAttribute("successMessage", "Role removed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/admin/users/" + id + "/edit";
    }

    /**
     * Display the manage books page.
     * 
     * @param page The page number
     * @param size The page size
     * @param search The search term
     * @param model The model
     * @return The manage books view
     */
    @GetMapping("/books")
    public String manageBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {
        
        Page<Book> books;
        
        if (search != null && !search.isEmpty()) {
            books = bookService.searchBooks(search, PageRequest.of(page, size, Sort.by("id").ascending()));
            model.addAttribute("search", search);
        } else {
            books = bookService.findAll(PageRequest.of(page, size, Sort.by("id").ascending()));
        }
        
        model.addAttribute("books", books);
        
        return "admin/manage-books";
    }

    /**
     * Activate or deactivate a book.
     * 
     * @param id The book ID
     * @param active The active status
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to manage books page
     */
    @PostMapping("/books/{id}/toggle-status")
    public String toggleBookStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            RedirectAttributes redirectAttributes) {
        
        try {
            bookService.setActive(id, active);
            String status = active ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("successMessage", "Book " + status + " successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/admin/books";
    }

    /**
     * Update book inventory.
     * 
     * @param id The book ID
     * @param totalCopies The total copies
     * @param availableCopies The available copies
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to manage books page
     */
    @PostMapping("/books/{id}/update-inventory")
    public String updateBookInventory(
            @PathVariable Long id,
            @RequestParam int totalCopies,
            @RequestParam int availableCopies,
            RedirectAttributes redirectAttributes) {
        
        try {
            Book book = bookService.updateTotalCopies(id, totalCopies);
            bookService.updateAvailableCopies(id, availableCopies);
            redirectAttributes.addFlashAttribute("successMessage", "Book inventory updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/admin/books";
    }

    /**
     * Display the reports page.
     * 
     * @param model The model
     * @return The reports view
     */
    @GetMapping("/reports")
    public String showReports(Model model) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        
        model.addAttribute("startDate", startOfMonth);
        model.addAttribute("endDate", now);
        
        return "admin/reports";
    }

    /**
     * Generate a transaction report.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @param model The model
     * @return The transaction report view
     */
    @GetMapping("/reports/transactions")
    public String generateTransactionReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        model.addAttribute("reportData", reportService.generateAllTransactionsReport(start, end));
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("reportType", "transactions");
        
        return "admin/report-result";
    }

    /**
     * Generate a revenue report.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @param model The model
     * @return The revenue report view
     */
    @GetMapping("/reports/revenue")
    public String generateRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        model.addAttribute("reportData", reportService.generateRevenueReport(start, end));
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("reportType", "revenue");
        
        return "admin/report-result";
    }

    /**
     * Generate an overdue books report.
     * 
     * @param model The model
     * @return The overdue report view
     */
    @GetMapping("/reports/overdue")
    public String generateOverdueReport(Model model) {
        model.addAttribute("reportData", reportService.generateOverdueReport());
        model.addAttribute("reportType", "overdue");
        
        return "admin/report-result";
    }

    /**
     * Generate a book popularity report.
     * 
     * @param limit The maximum number of books to include
     * @param model The model
     * @return The popularity report view
     */
    @GetMapping("/reports/popularity")
    public String generatePopularityReport(
            @RequestParam(defaultValue = "10") int limit,
            Model model) {
        
        model.addAttribute("reportData", reportService.generateBookPopularityReport(limit));
        model.addAttribute("limit", limit);
        model.addAttribute("reportType", "popularity");
        
        return "admin/report-result";
    }
}
