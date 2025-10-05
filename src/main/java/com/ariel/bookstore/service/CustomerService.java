package com.ariel.bookstore.service;

import com.ariel.bookstore.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {

    Customer create(Customer customer);
    Customer getById(UUID id);
    Customer getByEmail(String email);
    Page<Customer> list(Pageable pageable);
    Page<Customer> searchByName(String name, Pageable pageable);
    Customer update(UUID id, Customer changes);
    void delete(UUID id);
    void deleteAll();
}
