package com.cmpeq0.controlsystem.dao.repository;

import com.cmpeq0.controlsystem.entity.Secret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface SecretRepository extends JpaRepository<Secret, Long> {
}
