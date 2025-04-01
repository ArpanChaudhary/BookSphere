package com.booksphere.controller;

import com.booksphere.model.Book;
import com.booksphere.model.Notification;
import com.booksphere.model.User;
import com.booksphere.service.BookService;
import com.booksphere.service.NotificationService;
import com.booksphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for the home and landing pages.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final UserService userService;
    private final NotificationService notificationService;

    /**
     * Display the landing page.
     * 
     * @param model The model
     * @return The index page view
     */
    @GetMapping("/")
    public String showIndexPage(Model model) {
        // Get popular books for the landing page
        List<Book> popularBooks = bookService.findMostPopular(5);
        model.addAttribute("popularBooks", popularBooks);
        
        return "index";
    }

    /**
     * Display the dashboard appropriate for the user's role.
     * 
     * @param userDetails The authenticated user details
     * @param model The model
     * @return The appropriate dashboard view based on user role
     */
    @GetMapping("/dashboard")
    public String showDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        
        // Get unread notifications
        long unreadNotifications = notificationService.countUnreadByUser(user);
        model.addAttribute("unreadNotifications", unreadNotifications);
        
        // Determine which dashboard to show based on role
        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AUTHOR"))) {
            return "redirect:/author/dashboard";
        } else {
            // Default to user dashboard
            return "redirect:/user/dashboard";
        }
    }

    /**
     * Display the search page with results.
     * 
     * @param query The search query
     * @param page The page number
     * @param size The page size
     * @param model The model
     * @return The search results page view
     */
    @GetMapping("/search")
    public String searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        if (query == null || query.isEmpty()) {
            // If no query, show all available books
            Page<Book> books = bookService.findAvailable(PageRequest.of(page, size, Sort.by("title").ascending()));
            model.addAttribute("books", books);
            model.addAttribute("query", "");
        } else {
            // Search for books matching the query
            Page<Book> books = bookService.searchBooks(query, PageRequest.of(page, size, Sort.by("title").ascending()));
            model.addAttribute("books", books);
            model.addAttribute("query", query);
        }
        
        return "user/books";
    }
}
