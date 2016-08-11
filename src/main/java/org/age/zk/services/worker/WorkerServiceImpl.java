package org.age.zk.services.worker;

import com.google.common.eventbus.Subscribe;
import org.age.zk.services.AbstractService;
import org.age.zk.services.leadership.LeadershipService;
import org.age.zk.services.worker.event.InitializeEvent;
import org.age.zk.services.worker.event.StartComputationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WorkerServiceImpl extends AbstractService implements WorkerService {

    private static final Logger log = LoggerFactory.getLogger(WorkerServiceImpl.class);

    private WorkerState workerState = WorkerState.INIT;

    @Autowired
    private LeadershipService leadershipService;

    @Override
    public void start() {
        log.debug("Start worker service");
        running.set(true);

        try {
            createServiceNode(WorkerConst.COMPUTATION_STATE_NODE_PATH, GlobalComputationState.INIT.toBytes());
        } catch (Exception e) {
            log.error("Error starting worker service", e);
            return;
        }

        eventBus.post(new InitializeEvent());
        log.debug("Worker service started");
    }

    @Override
    public void stop(Runnable callback) {
        log.debug("Stop worker service with callback");
        stop();
        callback.run();
    }

    @Override
    public void stop() {
        log.debug("Stop worker service");
        running.set(false);
        log.debug("Worker service stopped");
    }

    @Override
    public int getPhase() {
        return 10;
    }

    @Subscribe
    public void initiliaze(InitializeEvent event) {
        log.debug("init event");
        if (workerState != WorkerState.INIT) {
            log.debug("Worker already initialized");
            return;
        }

        GlobalComputationState globalComputationState = getGlobalComputationState();
        if (globalComputationState == GlobalComputationState.FINISHED) {
            log.debug("Cluster in FINISHED state");
            eventBus.post(new StartComputationEvent());
            return;
        } else if (globalComputationState == GlobalComputationState.COMPUTING) {
            log.debug("Cluster already computing - join in!");
            eventBus.post(new StartComputationEvent());
            return;
        }

        if (!leadershipService.isMaster()) {
            log.info("Cannot start computation - current node is not a master");
            return;
        }

        // todo check if sufficient nodes

        log.info("Starting computation");
        setGlobalComputationState(GlobalComputationState.COMPUTING);
    }

    @Subscribe
    public void startComputation(StartComputationEvent event) throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);

        if (workerState == WorkerState.WORKING) {
            log.debug("Node already working");
            return;
        } else if (workerState == WorkerState.FINISHED) {
            log.debug("Computation already finished");
            return;
        }

        workerState = WorkerState.WORKING;
        // TODO: start work
        log.debug("EXECUTION STARTED");
    }

    private GlobalComputationState getGlobalComputationState() {
        byte[] rawData = zookeeperService.getRawData(WorkerConst.COMPUTATION_STATE_NODE_PATH);
        return GlobalComputationState.fromBytes(rawData);
    }

    private void setGlobalComputationState(GlobalComputationState state) {
        zookeeperService.setData(WorkerConst.COMPUTATION_STATE_NODE_PATH, state.toBytes());
    }
}
