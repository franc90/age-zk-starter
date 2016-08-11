package org.age.zk.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.google.common.base.MoreObjects.toStringHelper;

@Component
public class Simple implements Task {

    private static final Logger log = LoggerFactory.getLogger(Simple.class);

    @Override
    public void run() {
        log.info("This is the simplest possible example of a computation.");
    }

    @Override
    public String toString() {
        return toStringHelper(this).toString();
    }
}
