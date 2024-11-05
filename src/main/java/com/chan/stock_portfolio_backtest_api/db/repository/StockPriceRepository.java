package com.chan.stock_portfolio_backtest_api.db.repository;

import com.chan.stock_portfolio_backtest_api.db.entity.StockPrice;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockPriceRepository extends JpaRepository<StockPrice, Integer> {
    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.name = :name")
    List<StockPrice> findByStockName(@Param("name") String name);

    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.name = :name " +
            "AND sp.baseDate >= :startDate " +
            "AND sp.baseDate <= :endDate")
    List<StockPrice> findByStockNameAndDateRange(@Param("name") String name,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
}
