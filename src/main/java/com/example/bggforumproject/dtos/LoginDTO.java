package com.example.bggforumproject.dtos;


import jakarta.validation.constraints.NotBlank;

public record LoginDTO
        (
                @NotBlank(message = "Username is mandatory")
                String username,

                @NotBlank(message = "Password is mandatory")
                String password
        ) {
}
