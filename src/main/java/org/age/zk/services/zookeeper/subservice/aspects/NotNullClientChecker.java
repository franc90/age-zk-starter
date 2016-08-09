package org.age.zk.services.zookeeper.subservice.aspects;

import org.age.zk.services.zookeeper.subservice.ZookeeperSubService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NotNullClientChecker {

    @Before("execution(* org.age.zk.services.zookeeper.subservice.impl.*.*(..))")
    public void checkNotNullClient(JoinPoint joinPoint) {
        ZookeeperSubService target = (ZookeeperSubService) joinPoint.getTarget();
        target.checkClientNotNull();
    }

}
