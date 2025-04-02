package com.booksphere.controller;

import com.booksphere.dto.BookDto;
import com.booksphere.model.Book;
import com.booksphere.model.Notification;
import com.booksphere.model.Transaction;
import com.booksphere.model.User;
import com.booksphere.service.BookService;
import com.booksphere.service.NotificationService;
import com.booksphere.service.ReportService;
import com.booksphere.service.TransactionService;
import com.booksphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller for author operations.
 */
@Controller
@RequestMapping("/author")
@RequiredArgsConstructor
public class AuthorController {

    private final UserService userService;
    private final BookService bookService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    /**
     * Display the author dashboard.
     * 
     * @param userDetails The authenticated user details
     * @param model The model
     * @return The author dashboard view
     */
    @GetMapping("/dashboard")
    public String showDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        User author = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("author", author);
        
        // Get author's books
        Page<Book> books = bookService.findByAuthor(author, PageRequest.of(0, 5));
        model.addAttribute("books", books);
        
        // Get recent transactions for author's books
        Page<Transaction> transactions = transactionService.findTransactionsByAuthor(
                author.getId(), PageRequest.of(0, 5));
        model.addAttribute("transactions", transactions);
        
        // Get unread notifications
        List<Notification> notifications = notificationService.findUnreadByUser(author);
        model.addAttribute("notifications", notifications);
        
        // Get book rental statistics - most popular book
        List<Book> popularBooks = bookService.findMostPopular(1);
        if (!popularBooks.isEmpty()) {
            model.addAttribute("mostPopularBook", popularBooks.get(0));
        }
        
        return "author/dashboard";
    }

    /**
     * Display the author's books page.
     * 
     * @param userDetails The authenticated user details
     * @param page The page number
     * @param size The page size
     * @param model The model
     * @return The author's books view
     */
    @GetMapping("/books")
    public String showMyBooks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        User author = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("author", author);
        
        Page<Book> books = bookService.findByAuthor(author, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        model.addAttribute("books", books);
        
        return "author/my-books";
    }

    /**
     * Display the add book page.
     * 
     * @param model The model
     * @return The add book view
     */
    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        if (!model.containsAttribute("bookDto")) {
            model.addAttribute("bookDto", new BookDto());
        }
        return "author/add-book";
    }

    /**
     * Add a new book.
     * 
     * @param userDetails The authenticated user details
     * @param bookDto The book information
     * @param bindingResult Validation results
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to author's books page
     */
    @PostMapping("/books/add")
    public String addBook(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute("bookDto") BookDto bookDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookDto", bindingResult);
            redirectAttributes.addFlashAttribute("bookDto", bookDto);
            return "redirect:/author/books/add";
        }
        
        try {
            User author = userService.findByUsername(userDetails.getUsername());
            bookService.create(bookDto, author);
            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully");
            return "redirect:/author/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("bookDto", bookDto);
            return "redirect:/author/books/add";
        }
    }

    /**
     * Display the edit book page.
     * 
     * @param userDetails The authenticated user details
     * @param id The book ID
     * @param model The model
     * @return The edit book view
     */
    @GetMapping("/books/{id}/edit")
    public String showEditBookForm(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            Model model) {
        
        User author = userService.findByUsername(userDetails.getUsername());
        Book book = bookService.findById(id);
        
        // Ensure the book belongs to the author
        if (!book.getAuthor().getId().equals(author.getId())) {
            return "redirect:/author/books";
        }
        
        if (!model.containsAttribute("bookDto")) {
            BookDto bookDto = new BookDto();
            bookDto.setTitle(book.getTitle());
            bookDto.setDescription(book.getDescription());
            bookDto.setIsbn(book.getIsbn());
            bookDto.setAuthorId(book.getAuthor().getId());
            bookDto.setPublishedYear(book.getPublishedYear());
            bookDto.setPublisher(book.getPublisher());
            bookDto.setPrice(book.getPrice());
            bookDto.setRentalPrice(book.getRentalPrice());
            bookDto.setTotalCopies(book.getTotalCopies());
            bookDto.setAvailableCopies(book.getAvailableCopies());
            bookDto.setPublishedDate(book.getPublishedDate());
            bookDto.setCoverImage(book.getCoverImage());
            
            // Set genre IDs if any
            bookDto.setGenreIds(book.getGenres().stream()
                    .map(genre -> genre.getId())
                    .collect(java.util.stream.Collectors.toSet()));
            
            model.addAttribute("bookDto", bookDto);
        }
        
        model.addAttribute("book", book);
        
        return "author/edit-book";
    }

    /**
     * Update a book.
     * 
     * @param userDetails The authenticated user details
     * @param id The book ID
     * @param bookDto The updated book information
     * @param bindingResult Validation results
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to author's books page
     */
    @PostMapping("/books/{id}/edit")
    public String updateBook(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @ModelAttribute("bookDto") BookDto bookDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookDto", bindingResult);
            redirectAttributes.addFlashAttribute("bookDto", bookDto);
            return "redirect:/author/books/" + id + "/edit";
        }
        
        try {
            User author = userService.findByUsername(userDetails.getUsername());
            Book book = bookService.findById(id);
            
            // Ensure the book belongs to the author
            if (!book.getAuthor().getId().equals(author.getId())) {
                return "redirect:/author/books";
            }
            
            bookService.update(id, bookDto);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully");
            return "redirect:/author/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("bookDto", bookDto);
            return "redirect:/author/books/" + id + "/edit";
        }
    }

    /**
     * Display the book statistics page.
     * 
     * @param userDetails The authenticated user details
     * @param id The book ID
     * @param startDate The start date
     * @param endDate The end date
     * @param model The model
     * @return The book statistics view
     */
    @GetMapping("/books/{id}/statistics")
    public String showBookStatistics(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        User author = userService.findByUsername(userDetails.getUsername());
        Book book = bookService.findById(id);
        
        // Ensure the book belongs to the author
        if (!book.getAuthor().getId().equals(author.getId())) {
            return "redirect:/author/books";
        }
        
        // Set default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // Get statistics
        model.addAttribute("book", book);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        // Get rental statistics
        model.addAttribute("rentalStats", reportService.getBookRentalStatistics(
                book.getId(), startDate, endDate));
        
        // Get revenue statistics
        model.addAttribute("revenueStats", reportService.getBookRevenueStatistics(
                book.getId(), startDate, endDate));
        
        return "author/book-statistics";
    }

    /**
     * Display the author's overall statistics page.
     * 
     * @param userDetails The authenticated user details
     * @param startDate The start date
     * @param endDate The end date
     * @param model The model
     * @return The author statistics view
     */
    @GetMapping("/statistics")
    public String showAuthorStatistics(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        User author = userService.findByUsername(userDetails.getUsername());
        
        // Set default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // Get statistics
        model.addAttribute("author", author);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        // Get overall statistics
        model.addAttribute("overallStats", reportService.getAuthorOverallStatistics(
                author.getId(), startDate, endDate));
        
        // Get revenue statistics
        model.addAttribute("revenueStats", reportService.getAuthorRevenueStatistics(
                author.getId(), startDate, endDate));
        
        // Get popular books
        model.addAttribute("popularBooks", bookService.findMostPopular(5));
        
        return "author/statistics";
    }
}
