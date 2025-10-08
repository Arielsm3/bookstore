package com.ariel.bookstore.service;

import com.ariel.bookstore.dto.CustomerCreateRequest;
import com.ariel.bookstore.dto.CustomerResponse;
import com.ariel.bookstore.dto.CustomerUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {

    CustomerResponse create(CustomerCreateRequest request);
    CustomerResponse getById(UUID id);
    Page<CustomerResponse> list(Pageable pageable, String name);
    Page<CustomerResponse> searchByName(String name, Pageable pageable);
    CustomerResponse update(UUID id, CustomerUpdateRequest request);

    void delete(UUID id);
    void deleteAll();
}
