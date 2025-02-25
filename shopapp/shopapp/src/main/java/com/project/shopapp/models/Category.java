package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "categories")
public class  Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")//su dung khi khac nhau
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
}
