package com.example.bggforumproject.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "The DTO holds the info the front end must provide to update the user's info")
public record UserUpdateDTO(

        @Length(min = 4, max = 32, message = "First name must be between 4 and 32 characters")
        String firstName,

        @Length(min = 4, max = 32, message = "Last name must be between 4 and 32 characters")
        String lastName,

        String username,

        @Length(min = 6, message = "Password must be at least 6 characters long")
        String password,

        @NotBlank(message = "This field is mandatory")
        String passwordConfirm,

        @Email(regexp = emailRegex, message = "Email should be valid")
        @Length(max = 255, message = "Email should not be more than 255 characters")
        String email,

        @Email(regexp = phoneNumberRegex, message = "Phone number should be valid")
        String phoneNumber,

        MultipartFile profilePic

) {

        public static final String emailRegex = "^[a-zA-Z0-9]+([._-][0-9a-zA-Z]+)*@[a-zA-Z0-9]+([.-][0-9a-zA-Z]+)*\\.[a-zA-Z]{2,}$";
        public static final String phoneNumberRegex = "\\+\\d{3}\\s?\\(\\d{1,4}\\)\\s?\\d{3}-?\\s?\\d{3}$";
}
