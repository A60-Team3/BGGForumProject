package com.example.bggforumproject.security.alternative;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

// for future testing
public class CustomLogoutFilter extends SecurityContextLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        /* TODO authentication is empty find why, update logout method*/
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth == null) {
//            throw new CustomAuthenticationException("No logged user found");
//        }

        super.logout(request, response, authentication);
    }
}