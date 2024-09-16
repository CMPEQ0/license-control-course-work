package com.cmpeq0.controlsystem.dao.repository;

import com.cmpeq0.controlsystem.entity.PasswordRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface PasswordRecordRepository extends JpaRepository<PasswordRecord, Long> {

    PasswordRecord findPasswordRecordByLoginAndPasswordSHA(String login, String passwordSHA);

    PasswordRecord findPasswordRecordByLogin(String login);
}
