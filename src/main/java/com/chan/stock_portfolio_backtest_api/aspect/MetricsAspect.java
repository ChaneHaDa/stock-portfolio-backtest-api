package com.chan.stock_portfolio_backtest_api.aspect;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricsAspect {

    private final MeterRegistry meterRegistry;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object measureApiCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        Timer.Sample sample = Timer.start(meterRegistry);
        Counter.builder("api.calls.total")
            .tag("class", className)
            .tag("method", methodName)
            .register(meterRegistry)
            .increment();

        try {
            Object result = joinPoint.proceed();
            
            Counter.builder("api.calls.success")
                .tag("class", className)
                .tag("method", methodName)
                .register(meterRegistry)
                .increment();
                
            return result;
        } catch (Exception e) {
            Counter.builder("api.calls.error")
                .tag("class", className)
                .tag("method", methodName)
                .tag("exception", e.getClass().getSimpleName())
                .register(meterRegistry)
                .increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("api.response.time")
                .tag("class", className)
                .tag("method", methodName)
                .register(meterRegistry));
        }
    }
}