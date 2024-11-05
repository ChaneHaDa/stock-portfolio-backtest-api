package com.chan.stock_portfolio_backtest_api.db.repository;

import com.chan.stock_portfolio_backtest_api.db.dto.StockSearchDTO;
import com.chan.stock_portfolio_backtest_api.db.entity.Stock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    Stock findByShortCode(String query);

    Stock findByName(String query);

    @Query("SELECT new com.chan.stock_portfolio_backtest_api.db.dto.StockSearchDTO(e.name, e.shortCode, e.marketCategory) FROM Stock e WHERE e.name LIKE %:query% OR e.shortCode LIKE %:query%")
    List<StockSearchDTO> findByNameOrShortCodeContaining(@Param("query") String query);

    @Query("SELECT s FROM Stock s JOIN FETCH s.stockPriceList sp " +
            "WHERE s.name IN :names " +
            "AND sp.baseDate BETWEEN :startDate AND :endDate")
    List<Stock> findByNameInAndStockPriceDateRange(@Param("names") List<String> names,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
}