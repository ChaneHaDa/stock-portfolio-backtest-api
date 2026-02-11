package com.chan.stock_portfolio_backtest_api.index.repository;

import com.chan.stock_portfolio_backtest_api.index.domain.CalcIndexPrice;
import com.chan.stock_portfolio_backtest_api.index.domain.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalcIndexPriceRepository extends JpaRepository<CalcIndexPrice, Integer> {
    List<CalcIndexPrice> findByIndexInfoAndBaseDateBetween(IndexInfo indexInfo, LocalDate startDate, LocalDate endDate);
}
