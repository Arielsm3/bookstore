package com.ariel.bookstore.dto;

import java.util.UUID;

public record CustomerResponse(
        UUID id, String name, String email, String phone
) {
}
