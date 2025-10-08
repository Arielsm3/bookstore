package com.ariel.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        String phone
) {
}
