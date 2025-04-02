package com.booksphere.model;

/**
 * Enum representing different types of transactions in the BookSphere system.
 */
public enum TransactionType {
    CHECKOUT,    // When a user borrows a book
    RETURN,      // When a user returns a book
    RESERVE,     // When a user reserves a book
    PURCHASE,    // When a user purchases a book
    FINE_PAID    // When a user pays a fine
} 