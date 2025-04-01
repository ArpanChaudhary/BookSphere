package com.booksphere.controller;

import com.booksphere.model.Book;
import com.booksphere.model.User;
import com.booksphere.service.BookService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/**
 * Controller for book operations.
 */
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final UserService userService;
    private final TransactionService transactionService;

    /**
     * Display books catalog.
     * 
     * @param page The page number
     * @param size The page size
     * @param search The search term
     * @param genreId The genre ID filter
     * @param model The model
     * @return The books catalog view
     */
    @GetMapping
    public String listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long genreId,
            Model model) {
        
        Page<Book> books;
        
        if (search != null && !search.isEmpty()) {
            // Search for books
            books = bookService.searchBooks(search, PageRequest.of(page, size, Sort.by("title").ascending()));
            model.addAttribute("search", search);
        } else if (genreId != null) {
            // Filter by genre
            books = bookService.findByGenre(genreId, PageRequest.of(page, size, Sort.by("title").ascending()));
            model.addAttribute("genreId", genreId);
        } else {
            // Show all available books
            books = bookService.findAvailable(PageRequest.of(page, size, Sort.by("title").ascending()));
        }
        
        model.addAttribute("books", books);
        
        return "user/books";
    }

    /**
     * Display book details.
     * 
     * @param id The book ID
     * @param model The model
     * @return The book details view
     */
    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        
        return "user/book-details";
    }

    /**
     * Rent a book.
     * 
     * @param userDetails The authenticated user details
     * @param id The book ID
     * @param days The rental period in days
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to user's transactions page
     */
    @PostMapping("/{id}/rent")
    public String rentBook(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam(defaultValue = "14") int days,
            RedirectAttributes redirectAttributes) {
        
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            
            // Calculate due date
            LocalDateTime dueDate = LocalDateTime.now().plusDays(days);
            
            // Issue the book
            transactionService.issueBook(user.getId(), id, dueDate);
            
            redirectAttributes.addFlashAttribute("successMessage", "Book rented successfully");
            return "redirect:/user/transactions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/books/" + id;
        }
    }
}
