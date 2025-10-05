package com.ariel.bookstore.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderLineResponse(
        UUID id,
        UUID bookId,
        String bookTitle,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
