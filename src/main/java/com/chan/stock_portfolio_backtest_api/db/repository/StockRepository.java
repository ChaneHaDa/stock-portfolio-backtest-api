package com.chan.stock_portfolio_backtest_api.db.repository;

import com.chan.stock_portfolio_backtest_api.db.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Integer> {
}
