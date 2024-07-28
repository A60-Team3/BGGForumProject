package com.example.bggforumproject.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Schema(description = "The DTO holds the info the front end must provide to update the user's info")
public record UserUpdateDTO(

        @Size(min = 4, max = 32, message = "First name must be between 4 and 32 characters")
        String firstName,

        @Size(min = 4, max = 32, message = "Last name must be between 4 and 32 characters")
        String lastName,

        @NotBlank(message = "Username is mandatory")
        String username,

        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password,

        @Email(regexp = emailRegex, message = "Email should be valid")
        @Size(max = 255, message = "Email should not be more than 255 characters")
        String email) {

        public static final String emailRegex = "^[a-zA-Z0-9]+([._-][0-9a-zA-Z]+)*@[a-zA-Z0-9]+([.-][0-9a-zA-Z]+)*\\.[a-zA-Z]{2,}$";

}
