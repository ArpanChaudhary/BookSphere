package com.booksphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for book statistics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookStatisticsDto {

    private Long bookId;
    private String bookTitle;
    private Long totalRentals;
    private Long activeRentals;
    private Long completedRentals;
    private Long overdueRentals;
    private BigDecimal totalRevenue;
    private BigDecimal rentalRevenue;
    private BigDecimal lateFeeRevenue;
    private LocalDate startDate;
    private LocalDate endDate;
} 