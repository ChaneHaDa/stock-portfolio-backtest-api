package com.chan.stock_portfolio_backtest_api.service;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetricsService {

	private final MeterRegistry meterRegistry;
	private final AtomicInteger activeBacktests = new AtomicInteger(0);

	private Counter portfolioCreatedCounter;
	private Counter backtestExecutedSuccessCounter;
	private Counter backtestExecutedFailedCounter;
	private Timer backtestDurationTimer;
	private Counter stockDataLoadedCounter;

	@PostConstruct
	public void init() {
		// 메트릭을 한 번만 등록
		portfolioCreatedCounter = Counter.builder("portfolio.created.total")
			.description("Total number of portfolios created")
			.register(meterRegistry);

		backtestExecutedSuccessCounter = Counter.builder("backtest.executed.total")
			.description("Total number of successful backtests")
			.tag("status", "success")
			.register(meterRegistry);

		backtestExecutedFailedCounter = Counter.builder("backtest.executed.total")
			.description("Total number of failed backtests")
			.tag("status", "failed")
			.register(meterRegistry);

		backtestDurationTimer = Timer.builder("backtest.duration")
			.description("Backtest execution duration")
			.register(meterRegistry);

		stockDataLoadedCounter = Counter.builder("stock.data.loaded.total")
			.description("Total number of stock data points loaded")
			.register(meterRegistry);

		// databaseQueryTimer는 queryType 태그가 동적이므로 메서드에서 직접 생성

		// Gauge는 한 번만 등록
		Gauge.builder("backtest.active.count", activeBacktests, AtomicInteger::get)
			.description("Number of currently active backtests")
			.register(meterRegistry);
	}

	public void recordPortfolioCreated() {
		portfolioCreatedCounter.increment();
	}

	public void recordBacktestExecuted(String status) {
		if ("success".equals(status)) {
			backtestExecutedSuccessCounter.increment();
		} else if ("failed".equals(status)) {
			backtestExecutedFailedCounter.increment();
		}
	}

	public void recordBacktestDuration(Duration duration) {
		backtestDurationTimer.record(duration);
	}

	public void recordStockDataLoaded(int count) {
		stockDataLoadedCounter.increment(count);
	}

	public void incrementActiveBacktests() {
		activeBacktests.incrementAndGet();
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