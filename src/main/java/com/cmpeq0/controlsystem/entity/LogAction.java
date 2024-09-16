package com.cmpeq0.controlsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogAction {

    public enum ActionType {
        CREATE_RESOURCE,
        UPDATE_RESOURCE,
        DELETE_RESOURCE,
        ACCESS_RESOURCE,
        OPEN_DOOR,
        CLOSE_DOOR,
        CREATE_WORKER,
        CHANGE_WORKER,
        DELETE_WORKER,
        ASSIGN_RESOURCE,
        REVOKE_RESOURCE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long actorId;

    private long targetWorkerId;

    private long targetResourceId;

    @Enumerated(EnumType.STRING)
    private ActionType action;

    @CreatedDate
    private LocalDateTime created;


}
