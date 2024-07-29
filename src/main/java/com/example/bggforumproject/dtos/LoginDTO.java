package com.example.bggforumproject.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "This DTO holds username and password.",
        allowableValues = {"username", "password"})
public record LoginDTO
        (
                @NotBlank(message = "Username is mandatory")
                String username,

                @NotBlank(message = "Password is mandatory")
                String password
        ) {
}
