package com.cmpeq0.controlsystem.dao.repository;

import com.cmpeq0.controlsystem.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface WorkerRepository extends JpaRepository<Worker, Long> {

    Worker findWorkerByLogin(String login);
    Worker findWorkerById(Long id);

}
