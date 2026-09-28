package com.example.quizwiz.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
}