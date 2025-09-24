package com.chan.stock_portfolio_backtest_api.repository;

import com.chan.stock_portfolio_backtest_api.domain.CalcStockPrice;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CalcStockPriceRepository extends JpaRepository<CalcStockPrice, Integer> {
    
    @Query("SELECT c FROM CalcStockPrice c WHERE c.stock = :stock AND c.baseDate BETWEEN :startDate AND :endDate ORDER BY c.baseDate")
    List<CalcStockPrice> findByStockAndBaseDateBetween(@Param("stock") Stock stock, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT c FROM CalcStockPrice c WHERE c.stock IN :stocks AND c.baseDate BETWEEN :startDate AND :endDate ORDER BY c.stock.id, c.baseDate")
    List<CalcStockPrice> findByStockInAndBaseDateBetween(@Param("stocks") List<Stock> stocks, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
