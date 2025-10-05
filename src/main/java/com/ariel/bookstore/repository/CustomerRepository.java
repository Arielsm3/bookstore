package com.ariel.bookstore.repository;

import com.ariel.bookstore.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Page<Customer> findByNameContainingIgnoreCase(String fullName, Pageable pageable);
    Optional<Customer> findByEmailIgnoreCase(String email);

}
