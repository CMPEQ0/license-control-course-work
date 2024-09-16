package com.cmpeq0.controlsystem.dao.repository;

import com.cmpeq0.controlsystem.entity.LogAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogAction, Long> {
}
