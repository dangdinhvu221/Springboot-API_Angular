package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "products")
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 350)
    private String name;
    @Column(name = "price")
    private Float price;
    @Column(name = "thumbnail", length = 350)
    private String thumbnail;
    @Column(name = "descriptions",  length = 300)
    private String description;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

}
