package com.ariel.bookstore.exception;

import java.time.OffsetDateTime;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        OffsetDateTime timestamp,
        Map<String, String> fieldErrors
) {}
