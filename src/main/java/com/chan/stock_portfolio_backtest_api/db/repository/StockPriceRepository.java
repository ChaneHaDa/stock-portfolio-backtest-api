package com.chan.stock_portfolio_backtest_api.db.repository;

import com.chan.stock_portfolio_backtest_api.db.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Integer> {
}
