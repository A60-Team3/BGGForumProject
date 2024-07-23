package com.example.bggforumproject.security.caseTwo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationDTO(

        @NotBlank(message = "Username is mandatory")
        String username,

        @NotBlank(message = "Password is mandatory")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password,

        @NotBlank(message = "First name is mandatory")
        @Size(min = 4, max = 32, message = "First name must be between 4 and 32 characters")
        String firstName,

        @NotBlank(message = "Last name is mandatory")
        @Size(min = 4, max = 32, message = "Last name must be between 4 and 32 characters")
        String lastName,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        @Size(max = 255, message = "Email should not be more than 255 characters")
        String email
) {

}
