package com.booksphere.controller;

import com.booksphere.dto.BookDto;
import com.booksphere.dto.UserDto;
import com.booksphere.model.Book;
import com.booksphere.model.Genre;
import com.booksphere.model.Transaction;
import com.booksphere.model.User;
import com.booksphere.model.Role;
import com.booksphere.service.BookService;
import com.booksphere.service.FileService;
import com.booksphere.service.GenreService;
import com.booksphere.service.ReportService;
import com.booksphere.service.TransactionService;
import com.booksphere.service.UserService;
import com.booksphere.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for admin operations.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BookService bookService;
    private final TransactionService transactionService;
    private final ReportService reportService;
    private final GenreService genreService;
    private final FileService fileService;
    private final RoleService roleService;

    /**
     * Display the admin dashboard.
     * 
     * @param model The model
     * @return The admin dashboard view
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Get statistics for the dashboard
        model.addAttribute("totalBooks", bookService.countTotalBooks());
        model.addAttribute("activeRentals", transactionService.countActiveRentals());
        model.addAttribute("overdueBooks", transactionService.countOverdueBooks());
        model.addAttribute("totalUsers", userService.countTotalUsers());
        
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
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) String search,
            Model model) {
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> users;
        
        if (role != null && !role.isEmpty()) {
            users = userService.findByRole(role, pageRequest);
        } else if (status != null) {
            users = userService.findByEnabled(status, pageRequest);
        } else if (search != null && !search.isEmpty()) {
            users = userService.searchUsers(search, pageRequest);
        } else {
            users = userService.getAllUsers(pageRequest);
        }

        List<Role> roles = roleService.getAllRoles();
        
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        model.addAttribute("userDto", new UserDto());
        return "admin/users";
    }

    /**
     * Display the edit user page.
     * 
     * @param id The user ID
     * @param model The model
     * @return The edit user view
     */
    @GetMapping("/users/{id}")
    @ResponseBody
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Create a new user.
     * 
     * @param userDto The user information
     * @param result Binding result
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to manage users page
     */
    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute UserDto userDto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the errors below.");
            return "redirect:/admin/users";
        }

        try {
            userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("success", "User created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    /**
     * Update a user.
     * 
     * @param id The user ID
     * @param userDto The updated user information
     * @param result Binding result
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to manage users page
     */
    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id,
                           @Valid @ModelAttribute UserDto userDto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the errors below.");
            return "redirect:/admin/users";
        }

        try {
            userDto.setId(id);
            userService.updateUser(id, userDto);
            redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    /**
     * Delete a user.
     * 
     * @param id The user ID
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to manage users page
     */
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }

        return "redirect:/admin/users";
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
     * Display the add book page.
     * 
     * @param model The model
     * @return The add book view
     */
    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("bookDto", new BookDto());
        model.addAttribute("authors", userService.findByRole("AUTHOR"));
        model.addAttribute("genres", genreService.findAll());
        return "admin/book-form";
    }

    /**
     * Add a new book.
     * 
     * @param bookDto The book information
     * @param coverImage The book cover image
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to manage books page
     */
    @PostMapping("/books/add")
    public String addBook(@ModelAttribute BookDto bookDto,
                         @RequestParam(required = false) MultipartFile coverImage,
                         RedirectAttributes redirectAttributes) {
        try {
            if (coverImage != null && !coverImage.isEmpty()) {
                String imageUrl = fileService.saveFile(coverImage, "book-covers");
                bookDto.setCoverImage(imageUrl);
            }
            
            User author = userService.findById(bookDto.getAuthorId());
            bookService.create(bookDto, author);
            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully");
            return "redirect:/admin/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/books/add";
        }
    }

    /**
     * Display the edit book page.
     * 
     * @param id The book ID
     * @param model The model
     * @return The edit book view
     */
    @GetMapping("/books/{id}/edit")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setDescription(book.getDescription());
        bookDto.setAuthorId(book.getAuthor().getId());
        bookDto.setPublisher(book.getPublisher());
        bookDto.setPublishedYear(book.getPublishedYear());
        bookDto.setPublishedDate(book.getPublishedDate());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setPrice(book.getPrice());
        bookDto.setRentalPrice(book.getRentalPrice());
        bookDto.setTotalCopies(book.getTotalCopies());
        bookDto.setAvailableCopies(book.getAvailableCopies());
        bookDto.setGenreIds(book.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet()));
        
        model.addAttribute("book", book);
        model.addAttribute("bookDto", bookDto);
        model.addAttribute("authors", userService.findByRole("AUTHOR"));
        model.addAttribute("genres", genreService.findAll());
        return "admin/book-form";
    }

    /**
     * Update a book.
     * 
     * @param id The book ID
     * @param bookDto The updated book information
     * @param coverImage The book cover image
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to manage books page
     */
    @PostMapping("/books/{id}")
    public String updateBook(@PathVariable Long id,
                           @ModelAttribute BookDto bookDto,
                           @RequestParam(required = false) MultipartFile coverImage,
                           RedirectAttributes redirectAttributes) {
        try {
            if (coverImage != null && !coverImage.isEmpty()) {
                String imageUrl = fileService.saveFile(coverImage, "book-covers");
                bookDto.setCoverImage(imageUrl);
            }
            
            bookService.update(id, bookDto);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully");
            return "redirect:/admin/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/books/" + id + "/edit";
        }
    }

    /**
     * Delete a book.
     * 
     * @param id The book ID
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to manage books page
     */
    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/books";
    }

    /**
     * Activate or deactivate a book.
     * 
     * @param id The book ID
     * @param active The active status
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/books/{id}/toggle-status")
    public ResponseEntity<?> toggleBookStatus(@PathVariable Long id, @RequestParam boolean active) {
        try {
            bookService.setActive(id, active);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

    @GetMapping("/genres")
    public String showGenresManagement(Model model) {
        model.addAttribute("genres", genreService.findAll());
        return "admin/genres";
    }

    @PostMapping("/genres/add")
    public String addGenre(@ModelAttribute Genre genre, RedirectAttributes redirectAttributes) {
        try {
            genreService.createGenre(genre);
            redirectAttributes.addFlashAttribute("successMessage", "Genre added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/genres";
    }

    @GetMapping("/genres/{id}")
    @ResponseBody
    public Genre getGenre(@PathVariable Long id) {
        return genreService.getGenreById(id);
    }

    @PostMapping("/genres/{id}")
    public String updateGenre(@PathVariable Long id, @ModelAttribute Genre genre, RedirectAttributes redirectAttributes) {
        try {
            genreService.updateGenre(id, genre);
            redirectAttributes.addFlashAttribute("successMessage", "Genre updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/genres";
    }

    @PostMapping("/genres/{id}/delete")
    public String deleteGenre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            genreService.deleteGenre(id);
            redirectAttributes.addFlashAttribute("successMessage", "Genre deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/genres";
    }

    @PostMapping("/genres/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<?> toggleGenreStatus(@PathVariable Long id, @RequestParam boolean active) {
        try {
            Genre genre = genreService.getGenreById(id);
            genre.setActive(active);
            genreService.updateGenre(id, genre);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/authors")
    public String showAuthorsManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            Model model) {
        
        Page<User> authors;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        
        if (search != null && !search.isEmpty()) {
            authors = userService.searchUsers(search, pageable);
            model.addAttribute("search", search);
        } else {
            authors = userService.findByRole("AUTHOR", pageable);
        }
        
        if (status != null && !status.isEmpty()) {
            boolean enabled = "active".equals(status);
            authors = userService.findByEnabled(enabled, pageable);
            model.addAttribute("status", status);
        }
        
        if (role != null && !role.isEmpty()) {
            authors = userService.findByRole(role, pageable);
            model.addAttribute("role", role);
        }
        
        model.addAttribute("authors", authors);
        return "admin/authors";
    }

    @PostMapping("/authors/add")
    public String addAuthor(@ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        try {
            userDto.setRole("AUTHOR");
            userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage", "Author added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/authors";
    }

    @GetMapping("/authors/{id}/edit")
    public String showEditAuthorForm(@PathVariable Long id, Model model) {
        User author = userService.findById(id);
        UserDto userDto = new UserDto();
        userDto.setFirstName(author.getFirstName());
        userDto.setLastName(author.getLastName());
        userDto.setEmail(author.getEmail());
        userDto.setPhoneNumber(author.getPhoneNumber());
        userDto.setAddress(author.getAddress());
        
        model.addAttribute("author", author);
        model.addAttribute("userDto", userDto);
        return "admin/edit-author";
    }

    @PostMapping("/authors/{id}")
    public String updateAuthor(@PathVariable Long id, @ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(id, userDto);
            redirectAttributes.addFlashAttribute("successMessage", "Author updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/authors";
    }

    @PostMapping("/authors/{id}/delete")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Author deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/authors";
    }

    @PostMapping("/authors/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<?> toggleAuthorStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        try {
            userService.setEnabled(id, enabled);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/roles")
    public String showRolesManagement() {
        return "admin/roles";
    }

    @GetMapping("/transactions")
    public String showTransactionsManagement() {
        return "admin/transactions";
    }

    @GetMapping("/transactions/overdue")
    public String showOverdueTransactions() {
        return "admin/overdue-transactions";
    }

    @GetMapping("/fees")
    public String showLateFeesManagement() {
        return "admin/late-fees";
    }

    @GetMapping("/settings")
    public String showSystemSettings() {
        return "admin/settings";
    }

    @GetMapping("/logs")
    public String showSystemLogs() {
        return "admin/logs";
    }
}
