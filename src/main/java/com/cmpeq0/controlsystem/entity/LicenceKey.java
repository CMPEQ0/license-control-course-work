package com.cmpeq0.controlsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LicenceKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String key;

    @OneToOne
    @JoinColumn(name = "secret_id")
    private Secret secret;

}
