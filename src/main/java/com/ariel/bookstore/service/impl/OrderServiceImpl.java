package com.ariel.bookstore.service.impl;

import com.ariel.bookstore.dto.OrderCreateRequest;
import com.ariel.bookstore.dto.OrderLineRequest;
import com.ariel.bookstore.dto.OrderLineResponse;
import com.ariel.bookstore.dto.OrderResponse;
import com.ariel.bookstore.model.*;
import com.ariel.bookstore.repository.BookRepository;
import com.ariel.bookstore.repository.CustomerRepository;
import com.ariel.bookstore.repository.OrderRepository;
import com.ariel.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;

    @Override
    public OrderResponse create(OrderCreateRequest request) {
        var customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));

        var order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        order.setUpdatedAt(order.getCreatedAt());

        var total = BigDecimal.ZERO;
        for(OrderLineRequest lineRequest: request.lines()) {
            var book = bookRepository.findById(lineRequest.bookId())
                    .orElseThrow(() -> new NoSuchElementException("Book not found: " + lineRequest.bookId()));

            var line = new OrderLine();
            line.setOrder(order);
            line.setBook(book);
            line.setQuantity(lineRequest.quantity());
            line.setUnitPrice(book.getPrice());

            var lineTotal = book.getPrice().multiply(BigDecimal.valueOf(lineRequest.quantity()));
            line.setLineTotal(lineTotal);
            order.getLines().add(line);
            total = total.add(lineTotal);
        }

        order.setTotal(total);

        var saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getById(UUID id) {
        return orderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderResponse> list(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public OrderResponse markPaid(UUID id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
        order.setStatus(OrderStatus.PAID);
        order.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse cancel(UUID id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order order) {
        var lines = order.getLines().stream()
                .map(orderLine -> new OrderLineResponse(
                        orderLine.getId(),
                        orderLine.getBook().getId(),
                        orderLine.getBook().getTitle(),
                        orderLine.getQuantity(),
                        orderLine.getUnitPrice(),
                        orderLine.getLineTotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getStatus().name(),
                safeString(order.getCreatedAt()),
                safeString(order.getUpdatedAt()),
                order.getTotal(),
                lines
        );
    }

    private static String safeString(Object o) {
        return o == null ? null : o.toString();
    }
}
