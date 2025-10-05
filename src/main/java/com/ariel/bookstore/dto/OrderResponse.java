package com.ariel.bookstore.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID customerId,
        String customerName,
        String status,
        String createdAt,
        String updatedAt,
        BigDecimal total,
        List<OrderLineResponse> lines
) {
}
