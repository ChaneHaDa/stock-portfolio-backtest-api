package com.chan.stock_portfolio_backtest_api.db.repository;

import com.chan.stock_portfolio_backtest_api.db.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.db.entity.Stock;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    Stock findByShortCode(String query);
    Stock findByName(String query);
    @Query("SELECT new com.chan.stock_portfolio_backtest_api.db.dto.StockDTO(e.id, e.name, e.shortCode, e.isinCode, e.marketCategory) FROM Stock e WHERE e.name LIKE %:query% OR e.shortCode LIKE %:query%")
    List<StockDTO> findByNameOrShortCodeContaining(@Param("query") String query);
}