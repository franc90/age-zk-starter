package org.age.zk.tasks.listener;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.FutureCallback;
import org.age.zk.services.worker.event.ExitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskExecutionListener implements FutureCallback<Object> {

    private static final Logger log = LoggerFactory.getLogger(TaskExecutionListener.class);

    private final EventBus eventBus;

    @Autowired
    public TaskExecutionListener(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onSuccess(Object result) {
        log.debug("Task succeeded");
        eventBus.post(new ExitEvent());
    }

    @Override
    public void onFailure(Throwable t) {
        log.debug("Task failed");
        eventBus.post(new ExitEvent());
    }
}
