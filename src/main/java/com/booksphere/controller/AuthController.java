package com.booksphere.controller;

import com.booksphere.dto.LoginRequest;
import com.booksphere.dto.RegistrationRequest;
import com.booksphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Controller for handling authentication operations.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Display the login page.
     * 
     * @param model The model
     * @return The login page view
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "login";
    }

    /**
     * Display the registration page.
     * 
     * @param model The model
     * @return The registration page view
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("registrationRequest")) {
            model.addAttribute("registrationRequest", new RegistrationRequest());
        }
        return "register";
    }

    /**
     * Process user registration.
     * 
     * @param registrationRequest The registration request
     * @param bindingResult Validation results
     * @param redirectAttributes Attributes for redirect
     * @return Redirect to login page on success, or registration page on failure
     */
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("registrationRequest") RegistrationRequest registrationRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registrationRequest", bindingResult);
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            return "redirect:/register";
        }
        
        try {
            userService.register(registrationRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            return "redirect:/register";
        }
    }
}
