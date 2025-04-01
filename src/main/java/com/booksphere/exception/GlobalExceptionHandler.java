package com.booksphere.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the application.
 * Handles different types of exceptions and returns appropriate views.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException.
     * 
     * @param ex The exception
     * @param model The model
     * @return The 404 error page
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        log.error("Resource not found: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    /**
     * Handle UnauthorizedException.
     * 
     * @param ex The exception
     * @param model The model
     * @return The 403 error page
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleUnauthorizedException(UnauthorizedException ex, Model model) {
        log.error("Unauthorized access: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/403";
    }

    /**
     * Handle AccessDeniedException.
     * 
     * @param ex The exception
     * @param model The model
     * @return The 403 error page
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        log.error("Access denied: {}", ex.getMessage());
        model.addAttribute("errorMessage", "You do not have permission to access this resource");
        return "error/403";
    }

    /**
     * Handle BindException (validation errors).
     * 
     * @param ex The exception
     * @param request The request
     * @param redirectAttributes Redirect attributes
     * @return Redirect to the referring page
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBindException(
            BindException ex, 
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        
        log.error("Validation error: {}", ex.getMessage());
        
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            errors.add(error.getDefaultMessage());
        });
        
        redirectAttributes.addFlashAttribute("validationErrors", errors);
        
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    /**
     * Handle all other exceptions.
     * 
     * @param ex The exception
     * @param model The model
     * @return The 500 error page
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {
        log.error("Unexpected error occurred", ex);
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        model.addAttribute("errorDetails", ex.getMessage());
        return "error/500";
    }
}
