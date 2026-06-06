package com.pancosky.newcartrade.aspect;

import com.pancosky.newcartrade.annotation.OperationLog;
import com.pancosky.newcartrade.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - startTime;
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("module: {}, action: {}, userId: {}, duration: {}ms",
                operationLog.module(), operationLog.action(), userId, duration);

        return result;
    }
}
