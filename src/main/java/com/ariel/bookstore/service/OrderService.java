package com.ariel.bookstore.service;

import com.ariel.bookstore.dto.OrderCreateRequest;
import com.ariel.bookstore.dto.OrderResponse;
import com.ariel.bookstore.model.Order;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    OrderResponse create(OrderCreateRequest request);
    OrderResponse getById(UUID id);
    Page<OrderResponse> list(Pageable pageable, UUID customerId, String status);

    OrderResponse markPaid(UUID id);
    OrderResponse cancel(UUID id);

}
