package com.chan.stock_portfolio_backtest_api.repository;

import com.chan.stock_portfolio_backtest_api.domain.CalcIndexPrice;
import com.chan.stock_portfolio_backtest_api.domain.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalcIndexPriceRepository extends JpaRepository<CalcIndexPrice, Long> {
    List<CalcIndexPrice> findByIndexInfoAndBaseDateBetween(IndexInfo indexInfo, LocalDate startDate, LocalDate endDate);
}
