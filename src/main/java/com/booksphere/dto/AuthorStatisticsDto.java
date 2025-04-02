package com.booksphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for author statistics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorStatisticsDto {

    private Long authorId;
    private String authorName;
    private Long totalBooks;
    private Long totalRentals;
    private Long activeRentals;
    private Long completedRentals;
    private Long overdueRentals;
    private BigDecimal totalRevenue;
    private BigDecimal rentalRevenue;
    private BigDecimal lateFeeRevenue;
    private List<BookStatisticsDto> bookStatistics;
    private LocalDate startDate;
    private LocalDate endDate;
} 