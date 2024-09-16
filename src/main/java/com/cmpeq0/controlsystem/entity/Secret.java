package com.cmpeq0.controlsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cmpeq0.controlsystem.entity.Room.Status.CLOSED;
import static com.cmpeq0.controlsystem.entity.Room.Status.OPEN;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Secret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private SecretType type;

    private String name;

    public enum SecretType {
        LICENCE,
        ACCOUNT,
        ROOM
    }

    private String secretKey;

    @OneToOne(mappedBy = "secret")
    private Account account;

    @OneToOne(mappedBy = "secret")
    private LicenceKey licence;

    @OneToOne(mappedBy = "secret")
    private Room room;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "secrets")
    private Set<Worker> workers = new HashSet<>();

    // Добавляем метод для добавления работника
    public void addWorker(Worker worker) {
        this.workers.add(worker);
        worker.getSecrets().add(this);
    }

    // Добавляем метод для удаления работника
    public void removeWorker(Worker worker) {
        this.workers.remove(worker);
        worker.getSecrets().remove(this);
    }

    public String getStatusString() {
        boolean closed = getRoom().isClosed();
        return (!closed) ? "Открыто" : "Закрыто";
    }
}
