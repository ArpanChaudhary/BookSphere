package com.booksphere.config;

import com.booksphere.model.*;
import com.booksphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Initializes sample data for the BookSphere application.
 * This component runs once when the application starts.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize data if this is the first run
        if (userRepository.count() > 0) {
            return;
        }
        
        // Create roles if they don't exist
        if (roleRepository.count() == 0) {
            System.out.println("Creating default roles...");
            Role adminRole = new Role("ADMIN", "Administrator role with full access");
            Role userRole = new Role("USER", "Regular user with basic access");
            Role authorRole = new Role("AUTHOR", "Author with book management access");
            
            roleRepository.save(adminRole);
            roleRepository.save(userRole);
            roleRepository.save(authorRole);
            System.out.println("Default roles created successfully.");
        }
        
        // Get the roles
        Role adminRole = roleRepository.findByName("ADMIN")
            .orElseThrow(() -> new RuntimeException("Admin role not found"));
        Role userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new RuntimeException("User role not found"));
        Role authorRole = roleRepository.findByName("AUTHOR")
            .orElseThrow(() -> new RuntimeException("Author role not found"));

        // Create user accounts
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setEmail("admin@booksphere.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setActive(true);
        adminUser.setEnabled(true);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
        adminUser.setUserRole(UserRole.ADMIN);
        adminUser.addRole(adminRole);
        userRepository.save(adminUser);

        User regularUser = new User();
        regularUser.setUsername("user");
        regularUser.setPassword(passwordEncoder.encode("user123"));
        regularUser.setEmail("user@booksphere.com");
        regularUser.setFirstName("Regular");
        regularUser.setLastName("User");
        regularUser.setActive(true);
        regularUser.setEnabled(true);
        regularUser.setCreatedAt(LocalDateTime.now());
        regularUser.setUpdatedAt(LocalDateTime.now());
        regularUser.setUserRole(UserRole.USER);
        regularUser.addRole(userRole);
        userRepository.save(regularUser);

        User authorUser = new User();
        authorUser.setUsername("author");
        authorUser.setPassword(passwordEncoder.encode("author123"));
        authorUser.setEmail("author@booksphere.com");
        authorUser.setFirstName("Famous");
        authorUser.setLastName("Author");
        authorUser.setActive(true);
        authorUser.setEnabled(true);
        authorUser.setCreatedAt(LocalDateTime.now());
        authorUser.setUpdatedAt(LocalDateTime.now());
        authorUser.setUserRole(UserRole.AUTHOR);
        authorUser.addRole(authorRole);
        userRepository.save(authorUser);

        // Create genres
        Genre fiction = new Genre();
        fiction.setName("Fiction");
        fiction.setDescription("Fictional literature");
        genreRepository.save(fiction);

        Genre scienceFiction = new Genre();
        scienceFiction.setName("Science Fiction");
        scienceFiction.setDescription("Speculative fiction with scientific concepts");
        genreRepository.save(scienceFiction);

        Genre fantasy = new Genre();
        fantasy.setName("Fantasy");
        fantasy.setDescription("Fiction with magical or supernatural elements");
        genreRepository.save(fantasy);

        Genre selfHelp = new Genre();
        selfHelp.setName("Self-Help");
        selfHelp.setDescription("Books for personal improvement");
        genreRepository.save(selfHelp);

        // Create books
        Book book1 = new Book();
        book1.setTitle("The Great Adventure");
        book1.setAuthor(authorUser);
        book1.setIsbn("9781234567890");
        book1.setDescription("An epic tale of adventure and discovery");
        book1.setPublishedDate(LocalDate.of(2023, 1, 15));
        book1.setPublishedYear(2023);
        book1.setPrice(new BigDecimal("19.99"));
        book1.setRentalPrice(new BigDecimal("2.99"));
        book1.setAvailableCopies(10);
        book1.setTotalCopies(10);
        book1.setCoverImage("/images/book1.svg");
        book1.setGenre(fiction);
        book1.setCreatedAt(LocalDateTime.now());
        book1.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("Beyond the Stars");
        book2.setAuthor(authorUser);
        book2.setIsbn("9780987654321");
        book2.setDescription("A journey through the cosmos and human potential");
        book2.setPublishedDate(LocalDate.of(2022, 7, 22));
        book2.setPublishedYear(2022);
        book2.setPrice(new BigDecimal("24.99"));
        book2.setRentalPrice(new BigDecimal("3.99"));
        book2.setAvailableCopies(5);
        book2.setTotalCopies(5);
        book2.setCoverImage("/images/book2.svg");
        book2.setGenre(scienceFiction);
        book2.setCreatedAt(LocalDateTime.now());
        book2.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book2);

        Book book3 = new Book();
        book3.setTitle("The Secret Garden");
        book3.setAuthor(authorUser);
        book3.setIsbn("9783456789012");
        book3.setDescription("Discovering magic in everyday places");
        book3.setPublishedDate(LocalDate.of(2021, 12, 10));
        book3.setPublishedYear(2021);
        book3.setPrice(new BigDecimal("14.99"));
        book3.setRentalPrice(new BigDecimal("1.99"));
        book3.setAvailableCopies(7);
        book3.setTotalCopies(8);
        book3.setCoverImage("/images/book3.svg");
        book3.setGenre(fantasy);
        book3.setCreatedAt(LocalDateTime.now());
        book3.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book3);

        Book book4 = new Book();
        book4.setTitle("Mind Mastery");
        book4.setAuthor(authorUser);
        book4.setIsbn("9786789012345");
        book4.setDescription("Techniques for controlling your thoughts and achieving mental clarity");
        book4.setPublishedDate(LocalDate.of(2023, 3, 28));
        book4.setPublishedYear(2023);
        book4.setPrice(new BigDecimal("29.99"));
        book4.setRentalPrice(new BigDecimal("4.99"));
        book4.setAvailableCopies(3);
        book4.setTotalCopies(3);
        book4.setCoverImage("/images/book4.svg");
        book4.setGenre(selfHelp);
        book4.setCreatedAt(LocalDateTime.now());
        book4.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book4);

        // Create a sample transaction
        Transaction transaction = new Transaction();
        transaction.setUser(regularUser);
        transaction.setBook(book1);
        transaction.setIssueDate(LocalDateTime.now().minusDays(10));
        transaction.setDueDate(LocalDateTime.now().plusDays(20));
        transaction.setType(Transaction.TransactionType.ISSUE);
        transaction.setRentalPrice(book1.getRentalPrice());
        transaction.setPaid(true);
        transaction.setCreatedAt(LocalDateTime.now().minusDays(10));
        transactionRepository.save(transaction);

        // Create a sample notification
        Notification notification = new Notification();
        notification.setUser(regularUser);
        notification.setMessage("Welcome to BookSphere! Explore our collection of books.");
        notification.setType(Notification.NotificationType.SYSTEM);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}