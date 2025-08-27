package com.chan.stock_portfolio_backtest_api.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeBacktests = new AtomicInteger(0);

    public void recordPortfolioCreated() {
        Counter.builder("portfolio.created.total")
                .description("Total number of portfolios created")
                .register(meterRegistry)
                .increment();
    }

    public void recordBacktestExecuted(String status) {
        Counter.builder("backtest.executed.total")
                .description("Total number of backtests executed")
                .tag("status", status)
                .register(meterRegistry)
                .increment();
    }

    public void recordBacktestDuration(Duration duration) {
        Timer.builder("backtest.duration")
                .description("Backtest execution duration")
                .register(meterRegistry)
                .record(duration);
    }

    public void recordStockDataLoaded(int count) {
        Counter.builder("stock.data.loaded.total")
                .description("Total number of stock data points loaded")
                .register(meterRegistry)
                .increment(count);
    }

    public void incrementActiveBacktests() {
        activeBacktests.incrementAndGet();
        Gauge.builder("backtest.active.count", activeBacktests, AtomicInteger::get)
                .description("Number of currently active backtests")
                .register(meterRegistry);
    }

    public void decrementActiveBacktests() {
        activeBacktests.decrementAndGet();
    }

    public void recordDatabaseQueryTime(String queryType, Duration duration) {
        Timer.builder("database.query.duration")
                .description("Database query execution time")
                .tag("type", queryType)
                .register(meterRegistry)
                .record(duration);
    }
}