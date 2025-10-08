package com.ariel.bookstore.service.impl;

import com.ariel.bookstore.dto.CustomerCreateRequest;
import com.ariel.bookstore.dto.CustomerResponse;
import com.ariel.bookstore.dto.CustomerUpdateRequest;
import com.ariel.bookstore.model.Customer;
import com.ariel.bookstore.repository.CustomerRepository;
import com.ariel.bookstore.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    @Override
    public CustomerResponse create(CustomerCreateRequest request) {
        repository.findByEmailIgnoreCase(request.email()).ifPresent(c -> {
            throw new IllegalArgumentException("Email already exists");
        });
        Customer saved = repository.save(Customer.builder()
                .name(request.name()).email(request.email()).phone(request.phone()).build());
        return toResponse(saved);
    }

    @Override
    public CustomerResponse getById(UUID id) {
        return repository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));
    }

    @Override
    public Page<CustomerResponse> list(Pageable pageable, String name) {
        Page<Customer> customerPage = (name == null || name.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNameContainingIgnoreCase(name, pageable);

        return customerPage.map(this::toResponse);
    }

    @Override
    public Page<CustomerResponse> searchByName(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable).map(this::toResponse);
    }

    @Override
    public CustomerResponse update(UUID id, CustomerUpdateRequest request) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());

        try {
            return toResponse(repository.save(customer));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Email already in use");
        }
    }

    @Override
    public void delete(UUID id) {
        if(!repository.existsById(id)) throw new NoSuchElementException("Customer not found");
        repository.deleteById(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone());
    }
}
