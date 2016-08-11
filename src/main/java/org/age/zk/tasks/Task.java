package org.age.zk.tasks;

public interface Task extends Runnable {

    default String getName() {
        return getClass().getSimpleName();
    }

}
