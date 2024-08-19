package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.request.RegistrationDTO;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/BGGForum")
public class AuthenticationMvcController {

    private final AuthenticationService authenticationService;
    private final ModelMapper mapper;
    private final View error;

    public AuthenticationMvcController(AuthenticationService authenticationService, ModelMapper mapper, View error) {
        this.authenticationService = authenticationService;
        this.mapper = mapper;
        this.error = error;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpSession httpSession) {
        SecurityContextHolder.clearContext();
        httpSession.invalidate();
        return "redirect:/BGGForum/main?deleted";
    }

    @GetMapping("/register")
    public ModelAndView showRegisterPage(@ModelAttribute("userRegisterModel") RegistrationDTO registrationDTO, Model model) {
        return new ModelAndView("user-create");
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("userRegisterModel") @Valid RegistrationDTO register,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user-create";
        }

        if (!register.password().equals(register.passwordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password.");
            return "user-create";
        }

        try {
            User user = mapper.map(register, User.class);
            authenticationService.registerUser(user);
            return "redirect:/BGGForum/login?success";
        } catch (EntityDuplicateException e) {
            String field = e.getMessage().contains("email") ? "email" : "username";
            String errorCode = e.getMessage().contains("email") ? "email_error" : "username_error";

            bindingResult.rejectValue(field, errorCode, e.getMessage());
            return "user-create";
        }
    }
}