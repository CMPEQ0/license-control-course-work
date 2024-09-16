package com.cmpeq0.controlsystem.dao;

import com.cmpeq0.controlsystem.dao.repository.PasswordRecordRepository;
import com.cmpeq0.controlsystem.dao.repository.WorkerRepository;
import com.cmpeq0.controlsystem.entity.LogAction;
import com.cmpeq0.controlsystem.entity.PasswordRecord;
import com.cmpeq0.controlsystem.entity.Worker;
import com.cmpeq0.controlsystem.exception.DataException;
import com.cmpeq0.controlsystem.utils.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkerDao {

    private final PasswordRecordRepository passwordRecordRepository;
    private final WorkerRepository workerRepository;
    private final LogDao log;

    public List<Worker> findAll() {
        return workerRepository.findAll();
    }

    public Worker createWorker(Worker worker, String shaPassword) throws DataException {
        Worker search = workerRepository.findWorkerByLogin(worker.getLogin());
        if (search != null) {
            throw new DataException("работник с таким логином уже существует");
        }
        worker = workerRepository.save(worker);
        PasswordRecord record = PasswordRecord.builder()
                .login(worker.getLogin())
                .passwordSHA(shaPassword).build();
        passwordRecordRepository.save(record);

        log.log(LogAction.ActionType.CREATE_WORKER, null, worker.getId());

        return worker;
    }

    public Worker updateWorker(Worker worker) throws DataException {
        Worker old = workerRepository.findWorkerById(worker.getId());
        if (!old.getLogin().equals(worker.getLogin())) {
            PasswordRecord record = passwordRecordRepository.findPasswordRecordByLogin(old.getLogin());
            PasswordRecord other = passwordRecordRepository.findPasswordRecordByLogin(worker.getLogin());
            if (other != null) {
                throw new DataException("Логин занят");
            }
            record.setLogin(worker.getLogin());
            passwordRecordRepository.save(record);
        }

        log.log(LogAction.ActionType.CHANGE_WORKER, null, worker.getId());

        return workerRepository.save(worker);
    }

    public void updateWorkerPassword(String login, String shaPassword) {
        PasswordRecord old = passwordRecordRepository.findPasswordRecordByLogin(login);
        old.setPasswordSHA(shaPassword);
        passwordRecordRepository.save(old);
    }

    public void removeWorker(Long id) {
        log.log(LogAction.ActionType.DELETE_WORKER, null, id);
        workerRepository.deleteById(id);
    }

    public Worker findById(long id) {
        return workerRepository.findWorkerById(id);
    }

    public boolean matchLoginPassword(String login, String password) {
        PasswordRecord record = passwordRecordRepository.findPasswordRecordByLogin(login);
        if (record == null) {
            return false;
        } else {
            return Encoder.matchSHA(password, record.getPasswordSHA());
        }
    }

    public Worker findWorkerByLogin(String login) {
        return workerRepository.findWorkerByLogin(login);
    }

    public void startDefault() {
        if (workerRepository.findAll().isEmpty()) {
            PasswordRecord record = PasswordRecord.builder()
                    .login("ivan123")
                    .passwordSHA(Encoder.encrypt("Ivan123!")).build();

            passwordRecordRepository.save(record);

            Worker admin = Worker.builder()
                    .name("Гусев Иван")
                    .department("Администрирование")
                    .position("Системный администратор")
                    .login("ivan123")
                    .role(Worker.Role.ADMIN).build();
            workerRepository.save(admin);


        }
    }
}
