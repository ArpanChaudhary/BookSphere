package com.booksphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for book information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Size(max = 1000, message = "Description is too long")
    private String description;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^\\d{10}|\\d{13}$", message = "ISBN must be 10 or 13 digits")
    private String isbn;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotNull(message = "Publication year is required")
    @Min(value = 1000, message = "Publication year must be at least 1000")
    @Max(value = 9999, message = "Publication year must be at most 9999")
    private Integer publishedYear;

    @NotNull(message = "Total copies is required")
    @Min(value = 0, message = "Total copies cannot be negative")
    private Integer totalCopies;

    @NotNull(message = "Available copies is required")
    @Min(value = 0, message = "Available copies cannot be negative")
    private Integer availableCopies;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Rental price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Rental price cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Rental price must have at most 2 decimal places")
    private BigDecimal rentalPrice;

    private boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotBlank(message = "Publisher is required")
    @Size(max = 255, message = "Publisher name is too long")
    private String publisher;

    private LocalDate publishedDate;

    private String coverImage;

    private Set<Long> genreIds = new HashSet<>();
}
