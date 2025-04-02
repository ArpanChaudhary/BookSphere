package com.booksphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for genre information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {

    private Long id;

    @NotBlank(message = "Genre name is required")
    @Size(max = 100, message = "Genre name is too long")
    private String name;

    @Size(max = 500, message = "Description is too long")
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
} 