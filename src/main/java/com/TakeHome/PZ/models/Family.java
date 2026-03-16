package com.TakeHome.PZ.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "app_families")
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "family")
    @Builder.Default
    private List<User> members = new ArrayList<>();
}