package org.age.zk.services.worker;

import com.google.common.eventbus.Subscribe;
import org.age.zk.services.AbstractService;
import org.age.zk.services.discovery.DiscoveryService;
import org.age.zk.services.leadership.LeadershipService;
import org.age.zk.services.worker.computation.ComputationManager;
import org.age.zk.services.worker.event.ExitEvent;
import org.age.zk.services.worker.event.InitializeEvent;
import org.age.zk.services.worker.event.StartComputationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WorkerServiceImpl extends AbstractService implements WorkerService {

    private static final Logger log = LoggerFactory.getLogger(WorkerServiceImpl.class);

    private WorkerState workerState = WorkerState.INIT;

    @Autowired
    private LeadershipService leadershipService;

    @Autowired
    private ComputationManager computationManager;

    @Autowired
    private DiscoveryService discoveryService;

    @Value("${cluster.minimal.clients:1}")
    private int minimalNumberOfClients;

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
        computationManager.shutdown();
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

        int nodesInTopology = discoveryService.getNodesCount();
        if (minimalNumberOfClients > nodesInTopology) {
            log.info("Waiting for more nodes. [{} of {}]", nodesInTopology, minimalNumberOfClients);
            return;
        }

        log.info("Starting computation");
        setGlobalComputationState(GlobalComputationState.COMPUTING);
        eventBus.post(new InitializeEvent());
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
        computationManager.startTask();
    }

    @Subscribe
    public void terminate(ExitEvent exitEvent) {
        log.debug("terminating");
        setGlobalComputationState(GlobalComputationState.FINISHED);
        computationManager.shutdown();
        System.exit(0);
    }

    private GlobalComputationState getGlobalComputationState() {
        byte[] rawData = zookeeperService.getRawData(WorkerConst.COMPUTATION_STATE_NODE_PATH);
        return GlobalComputationState.fromBytes(rawData);
    }

    private void setGlobalComputationState(GlobalComputationState state) {
        zookeeperService.setData(WorkerConst.COMPUTATION_STATE_NODE_PATH, state.toBytes());
    }
}
