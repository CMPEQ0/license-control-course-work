package com.cmpeq0.controlsystem.dao;

import com.cmpeq0.controlsystem.dao.repository.*;
import com.cmpeq0.controlsystem.entity.*;
import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.utils.SecretKeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecretDao {

    private final SecretRepository secretRepository;
    private final RoomRepository roomRepository;
    private final AccountRepository accountRepository;
    private final LicenceRepository licenceRepository;
    private final WorkerRepository workerRepository;
    private final LogDao log;

    private Secret createSecret(Secret.SecretType type, String name, String key) {
        Secret secret = Secret.builder()
                .type(type)
                .name(name)
                .secretKey(key)
                .workers(new HashSet<>())
                .build();

        //secret.addWorker(uiStateComponent.getActiveUser());
        secret = secretRepository.save(secret);
        return secret;
    }

    public Secret createRoom(Room room) {
        String key = SecretKeyManager.generateKey();
        Secret secret = createSecret(Secret.SecretType.ROOM, room.getName(), key);
        room.setSecret(secret);
        roomRepository.save(room);

        log.log(LogAction.ActionType.CREATE_RESOURCE, secret.getId(), null);

        return secret;
    }

    public Secret createAccount(Account account) {
        String key = SecretKeyManager.generateKey();
        account.setLogin(SecretKeyManager.encrypt(account.getLogin(), key));
        account.setPassword(SecretKeyManager.encrypt(account.getPassword(), key));
        Secret secret =  createSecret(Secret.SecretType.ACCOUNT, account.getName(), key);
        account.setSecret(secret);
        accountRepository.save(account);

        log.log(LogAction.ActionType.CREATE_RESOURCE, secret.getId(), null);

        return secret;
    }

    public Secret createLicenceKey(LicenceKey licenceKey) {
        String key = SecretKeyManager.generateKey();
        licenceKey.setKey(SecretKeyManager.encrypt(licenceKey.getKey(), key));
        Secret secret =  createSecret(Secret.SecretType.LICENCE, licenceKey.getName(), key);
        licenceKey.setSecret(secret);
        licenceRepository.save(licenceKey);

        log.log(LogAction.ActionType.CREATE_RESOURCE, secret.getId(), null);

        return secret;
    }

    @Transactional
    public void addSecretToWorker(Long workerId, Long secretId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        Secret secret = secretRepository.findById(secretId)
                .orElseThrow(() -> new RuntimeException("Secret not found"));

        // Добавляем секрет к работнику и сохраняем изменения
        worker.addSecret(secret);
        workerRepository.save(worker);

        log.log(LogAction.ActionType.ASSIGN_RESOURCE, secretId, workerId);
    }

    @Transactional
    public void removeSecretFromWorker(Long workerId, Long secretId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        Secret secret = secretRepository.findById(secretId)
                .orElseThrow(() -> new RuntimeException("Secret not found"));

        // Удаляем секрет от работника и сохраняем изменения
        worker.removeSecret(secret);
        workerRepository.save(worker);

        log.log(LogAction.ActionType.REVOKE_RESOURCE, secretId, workerId);
    }

    @Transactional
    public void clearWorkerSecrets(Long workerId) {
        Worker worker = workerRepository.findById(workerId).orElseThrow(() -> new RuntimeException("Worker not found"));
        for (Secret secret : worker.getSecrets()) {
            removeSecretFromWorker(workerId, secret.getId());
        }
    }

    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    @Transactional
    public void removeSecret(Long secretId) {
        Secret secret = secretRepository.findById(secretId)
                .orElseThrow(() -> new RuntimeException("Secret not found"));
        for (Worker worker : secret.getWorkers()) {
            removeSecretFromWorker(worker.getId(), secret.getId());
        }
        secretRepository.delete(secret);
    }

    @Transactional
    public void removeRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        log.log(LogAction.ActionType.DELETE_RESOURCE, room.getSecret().getId(), null);
        removeSecret(room.getSecret().getId());
        roomRepository.delete(room);
    }

    @Transactional
    public void removeLicence(Long licenceKeyId) {
        LicenceKey licenceKey = licenceRepository.findById(licenceKeyId)
                .orElseThrow(() -> new RuntimeException("License not found"));
        log.log(LogAction.ActionType.DELETE_RESOURCE, licenceKey.getSecret().getId(), null);
        removeSecret(licenceKey.getSecret().getId());
        licenceRepository.delete(licenceKey);


    }

    public void updateRoom(Room room) {
        roomRepository.save(room);
        log.log(LogAction.ActionType.UPDATE_RESOURCE, room.getSecret().getId(), null);
    }

    public void updateLicence(LicenceKey licenceKey) {
        licenceRepository.save(licenceKey);
        log.log(LogAction.ActionType.UPDATE_RESOURCE, licenceKey.getSecret().getId(), null);
    }

    public Secret findSecretById(long id) {
        return secretRepository.findById(id).orElse(null);
    }

    public List<Secret> findAllLicensesAndAccounts() {
        return secretRepository.findAll().stream().filter(item -> item.getType() != Secret.SecretType.ROOM).toList();
    }

    public void updateAccount(Account account) {
        accountRepository.save(account);
        log.log(LogAction.ActionType.UPDATE_RESOURCE, account.getSecret().getId(), null);
    }

    @Transactional
    public void removeAccount(long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        log.log(LogAction.ActionType.DELETE_RESOURCE, account.getSecret().getId(), null);
        removeSecret(account.getSecret().getId());
        accountRepository.delete(account);
    }

    public void openRoom(Room room) {
        room.setClosed(false);
        roomRepository.save(room);
        log.log(LogAction.ActionType.UPDATE_RESOURCE, room.getSecret().getId(), null);
    }
    public void closeRoom(Room room) {
        room.setClosed(true);
        roomRepository.save(room);
        log.log(LogAction.ActionType.UPDATE_RESOURCE, room.getSecret().getId(), null);
    }

    public void openAllRooms() {
        findAllRooms().forEach(this::openRoom);
    }

    public void closeAllRooms() {
        findAllRooms().forEach(this::closeRoom);
    }
}
