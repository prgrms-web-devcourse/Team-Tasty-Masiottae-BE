package com.tasty.masiottae.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before(value = "baseExceptionHandler()")
    public void exceptionHanlderWarningLogAdvice(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args[0] instanceof Exception) {
            log.error("{}", makeFormattedString(
                "<Exception type>", args[0].getClass().getCanonicalName(), true));
            log.error("{}\n", makeFormattedString(
                "<Default message>", ((Exception) args[0]).getMessage(), true));
        }
    }

    @AfterReturning(
        pointcut = "globalExceptionHandler()",
        returning = "response")
    public void afterReturningFindAccountsAdvice(
        JoinPoint joinPoint, ResponseEntity<ErrorResponse> response) {
        log.error("{}", makeFormattedString(
            "Handler", joinPoint.getSignature().toShortString(), false));
        log.error("{}", makeFormattedString(
            "Message", response.getBody().getMessage(), false));
        log.error("{}\n", makeFormattedString(
            "Status", response.getStatusCode().toString(), false));
    }

    @Pointcut("execution(* com.tasty.masiottae.common.exception.BaseExceptionHandler.handle*(..))")
    private void baseExceptionHandler() {}

    @Pointcut("execution(* com.tasty.masiottae.common.exception.GlobalExceptionHandler.handle*(..))")
    private void globalExceptionHandler() {}

    private String makeFormattedString(String key, String value, boolean isBase) {
        return isBase ? String.format("%-15s -> %s", key, value) : String.format("%-7s -> %s", key, value);
    }

}
