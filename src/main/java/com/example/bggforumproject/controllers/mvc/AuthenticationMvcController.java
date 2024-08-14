package com.example.bggforumproject.controllers.mvc;

import com.example.bggforumproject.dtos.RegistrationDTO;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.AuthenticationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController {

    private final AuthenticationService authenticationService;
    private final ModelMapper mapper;

    public AuthenticationMvcController(AuthenticationService authenticationService, ModelMapper mapper) {
        this.authenticationService = authenticationService;
        this.mapper = mapper;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logoutUser() {
        return "redirect:/auth/logout?true";
    }

    @PostMapping("/logout?true")
    public void handleLogout() {
    }

    @GetMapping("/register")
    public ModelAndView showRegisterPage(@ModelAttribute("userRegisterModel") RegistrationDTO registrationDTO, Model model) {
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("userRegisterModel") @Valid RegistrationDTO register,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (!register.password().equals(register.passwordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password.");
            return "register";
        }

        try {
            User user = mapper.map(register, User.class);
            authenticationService.registerUser(user);
            return "redirect:/auth/login?success";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("username", "username_error", e.getMessage());
            return "register";
        }
    }
}