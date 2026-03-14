package com.TakeHome.PZ.models;

import com.TakeHome.PZ.models.Enums.Role;
import com.TakeHome.PZ.models.Enums.Theme;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "app_users")
public class User {

    @Id
    private String id;

    private String name;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Theme theme = Theme.LIGHT;

    @ManyToOne
    private Wallpaper activeWallpaper;

    @ManyToMany
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}