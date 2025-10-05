package com.ariel.bookstore.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrderCreateRequest(
        @NotNull UUID customerId,
        @NotNull List<OrderLineRequest> lines
        ) {
}
