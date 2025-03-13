package com.chan.stock_portfolio_backtest_api.repository;

import com.chan.stock_portfolio_backtest_api.domain.CalcStockPrice;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalcStockPriceRepository extends JpaRepository<CalcStockPrice, Integer> {
    List<CalcStockPrice> findByStockAndBaseDateBetween(Stock stock, LocalDate startDate, LocalDate endDate);
}
