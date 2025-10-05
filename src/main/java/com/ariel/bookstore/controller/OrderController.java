package com.ariel.bookstore.controller;

import com.ariel.bookstore.dto.OrderCreateRequest;
import com.ariel.bookstore.dto.OrderResponse;
import com.ariel.bookstore.model.Order;
import com.ariel.bookstore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request) {
        OrderResponse created = service.create(request);

        return ResponseEntity
                .created(URI.create("/api/orders/" + created.id()))
                .body(created);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<OrderResponse> list(Pageable pageable) {
        return service.list(pageable);
    }
}
