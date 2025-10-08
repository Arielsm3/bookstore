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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;

    @Override
    public OrderResponse create(OrderCreateRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        order.setUpdatedAt(order.getCreatedAt());

        if(order.getLines() == null) {
            order.setLines(new ArrayList<>());
        }

        BigDecimal total = BigDecimal.ZERO;
        List<Book> updatedBooks = new ArrayList<>();

        for(OrderLineRequest lineRequest: request.lines()) {
            Book book = bookRepository.findById(lineRequest.bookId())
                    .orElseThrow(() -> new NoSuchElementException("Book not found: " + lineRequest.bookId()));

            int quantity = lineRequest.quantity();
            if(quantity <= 0) throw new IllegalArgumentException("Minimum quantity required is 1.");

            int stock = Optional.ofNullable(book.getStock()).orElse(0);
            if(stock < quantity) throw new IllegalArgumentException("Not enough available in stock.");

            book.setStock(stock - quantity);
            updatedBooks.add(book);

            OrderLine line = new OrderLine();
            line.setOrder(order);
            line.setBook(book);
            line.setQuantity(quantity);
            line.setUnitPrice(book.getPrice());

            BigDecimal lineTotal = book.getPrice().multiply(BigDecimal.valueOf(quantity));
            line.setLineTotal(lineTotal);

            order.getLines().add(line);
            total = total.add(lineTotal);
        }

        order.setTotal(total);

        Order saved = orderRepository.save(order);
        bookRepository.saveAll(updatedBooks);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(UUID id) {
        return orderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> list(Pageable pageable, UUID customerId, String status) {
        OrderStatus orderStatus = null;
        if(status != null && !status.isBlank()) {
            try {
                orderStatus = OrderStatus.valueOf(status.trim().toUpperCase());
            }
            catch(IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        }

        Page<Order> page;

        if(customerId != null && orderStatus != null) {
            page = orderRepository.findByCustomerIdStatus(customerId, orderStatus, pageable);
        }
        else if(customerId != null) {
            page = orderRepository.findByCustomerId(customerId, pageable);
        }
        else if(orderStatus != null) {
            page = orderRepository.findByStatus(orderStatus, pageable);
        }
        else { // if customerId == null && orderStatus == null
            page = orderRepository.findAll(pageable);
        }

        return page.map(this::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse markPaid(UUID id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
        order.setStatus(OrderStatus.PAID);
        order.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderLineResponse> lines = order.getLines().stream()
                .map(line -> new OrderLineResponse(
                        line.getId(),
                        line.getBook().getId(),
                        line.getBook().getTitle(),
                        line.getQuantity(),
                        line.getUnitPrice(),
                        line.getLineTotal()
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
