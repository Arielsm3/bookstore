package com.ariel.bookstore.service.impl;

import com.ariel.bookstore.model.Customer;
import com.ariel.bookstore.repository.CustomerRepository;
import com.ariel.bookstore.service.CustomerService;
import lombok.RequiredArgsConstructor;
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
    public Customer create(Customer customer) {
        repository.findByEmailIgnoreCase(customer.getEmail()).ifPresent(existing -> {
            throw new IllegalArgumentException("Email already in use!");
        });
        return repository.save(customer);
    }

    @Override
    public Customer getById(UUID id) {
        return repository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Customer not found!"));
    }

    @Override
    public Customer getByEmail(String email) {
        return repository.findByEmailIgnoreCase(email).orElseThrow(() ->
                new NoSuchElementException("Customer not found"));
    }

    @Override
    public Page<Customer> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Customer> searchByName(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Customer update(UUID id, Customer changes) {
        Customer current = getById(id);

        if(changes.getEmail() != null && !changes.getEmail().isBlank()
        && !changes.getEmail().equalsIgnoreCase(current.getEmail())) {
            repository.findByEmailIgnoreCase(changes.getEmail()).ifPresent(existing -> {
                throw new IllegalArgumentException("Email already in use!");
            });
            current.setEmail(changes.getEmail());
        }

        if(changes.getName() != null && !changes.getName().isBlank()) {
            current.setName(changes.getName());
        }

        return repository.save(current);
    }

    @Override
    public void delete(UUID id) {
        if(!repository.existsById(id)) {
            throw new NoSuchElementException("Customer not found!");
        }
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
