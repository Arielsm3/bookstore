package com.ariel.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "Books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 120)
    @NotBlank
    private String title;

    @Column(nullable = false, length = 120)
    @NotBlank
    private String author;

    @Column(nullable = false, unique = true, length = 30)
    @NotBlank
    private String isbn;

    @Column(nullable = false, precision = 12, scale = 2)
    @DecimalMin("0.00")
    private BigDecimal price;

    @PositiveOrZero
    private Integer stock;
}
