package org.age.zk.tasks;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RandomlyBreaking extends SimpleLongRunning {

    private static final Logger log = LoggerFactory.getLogger(RandomlyBreaking.class);

    private final int initialIterations;

    private final double exceptionProbability;

    @Autowired
    public RandomlyBreaking(@Value("${rand.task.initial.iterations:10}") int initialIterations,
                            @Value("${rand.task.exception.probability:0.3}") double exceptionProbability) {
        this.initialIterations = initialIterations;
        this.exceptionProbability = exceptionProbability;
    }

    @Override
    protected void additionalAction(int iteration) {
        if (iteration > initialIterations) {
            double randomValue = RandomUtils.nextDouble(0.0, 1.0);
            if (randomValue < exceptionProbability) {
                log.debug("{} < {}, exiting", randomValue, exceptionProbability);
                throw new RuntimeException();
            }
        }
    }
}
