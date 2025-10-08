package com.ariel.bookstore.controller;

import com.ariel.bookstore.dto.CustomerCreateRequest;
import com.ariel.bookstore.dto.CustomerResponse;
import com.ariel.bookstore.dto.CustomerUpdateRequest;
import com.ariel.bookstore.model.Customer;
import com.ariel.bookstore.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerCreateRequest request) {
        CustomerResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/customers/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<CustomerResponse> list(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String name
    ) {
        return service.list(pageable, name);
    }

    @GetMapping("/{id}")
    public CustomerResponse update(@PathVariable UUID id, @Valid @RequestBody CustomerUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
