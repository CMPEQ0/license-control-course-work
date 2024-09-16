package com.cmpeq0.controlsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Worker {

    public enum Role {
        ADMIN,
        USER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String department;

    private String position;

    private String login;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "worker_secret",
            joinColumns = @JoinColumn(name = "worker_id"),
            inverseJoinColumns = @JoinColumn(name = "secret_id")
    )
    private Set<Secret> secrets = new HashSet<>();

    // Добавляем метод для добавления секрета
    public void addSecret(Secret secret) {
        this.secrets.add(secret);
        secret.getWorkers().add(this);
    }

    // Добавляем метод для удаления секрета
    public void removeSecret(Secret secret) {
        this.secrets.remove(secret);
        secret.getWorkers().remove(this);
    }
}
