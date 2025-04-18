package com.chan.stock_portfolio_backtest_api.repository;

import com.chan.stock_portfolio_backtest_api.domain.StockNameHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockNameHistoryRepository extends JpaRepository<StockNameHistory, Integer> {
}
