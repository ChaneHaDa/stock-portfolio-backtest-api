package com.chan.stock_portfolio_backtest_api.repository;

import com.chan.stock_portfolio_backtest_api.domain.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockPriceRepository extends JpaRepository<StockPrice, Integer> {
    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :id")
    List<StockPrice> findByStockId(@Param("id") Integer id);

    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :id " +
            "AND sp.baseDate >= :startDate " +
            "AND sp.baseDate <= :endDate")
    List<StockPrice> findByStockIdAndDateRange(@Param("id") Integer id,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
}
