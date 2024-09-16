package com.cmpeq0.controlsystem.dao;

import com.cmpeq0.controlsystem.dao.repository.LogRepository;
import com.cmpeq0.controlsystem.entity.LogAction;
import com.cmpeq0.controlsystem.entity.Secret;
import com.cmpeq0.controlsystem.entity.Worker;
import com.cmpeq0.controlsystem.ui.UiStateComponent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class LogDao {

    private final LogRepository logRepository;
    private final UiStateComponent uiStateComponent;
    private final Logger logger = LoggerFactory.getLogger(LogDao.class);

    public void log(LogAction.ActionType action, Long secretId, Long workerId) {
        var builder = LogAction.builder().action(action).actorId(uiStateComponent.getActiveUser().getId());
        if (secretId == null) {
            builder = builder.targetResourceId(-1);
        } else {
            builder = builder.targetResourceId(secretId);
        }
        if (workerId == null) {
            builder = builder.targetWorkerId(-1);
        } else {
            builder = builder.targetWorkerId(workerId);
        }
        var data = builder.created(LocalDateTime.now()).build();
        logRepository.save(data);
        logger.trace("USER ACTION:: User " + data.getActorId() + " made action " + data.getAction().toString() + " at " + data.getCreated().toString());
    }

}
