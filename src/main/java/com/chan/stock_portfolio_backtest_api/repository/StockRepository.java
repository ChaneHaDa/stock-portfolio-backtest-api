package com.chan.stock_portfolio_backtest_api.repository;

import com.chan.stock_portfolio_backtest_api.dto.StockSearchDTO;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    Stock findByShortCode(String query);

    Stock findByName(String query);

    @Query("SELECT new com.chan.stock_portfolio_backtest_api.db.dto.StockSearchDTO(s.name, s.shortCode, s.marketCategory) FROM Stock s WHERE s.name LIKE %:query% OR s.shortCode LIKE %:query%")
    List<StockSearchDTO> findByNameOrShortCodeContaining(@Param("query") String query);

    @Query("SELECT s FROM Stock s JOIN FETCH s.calcStockPriceList sp " +
            "WHERE s.name IN :names " +
            "AND sp.baseDate BETWEEN :startDate AND :endDate")
    List<Stock> findByNameInAndStockPriceDateRange(@Param("names") List<String> names,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
}