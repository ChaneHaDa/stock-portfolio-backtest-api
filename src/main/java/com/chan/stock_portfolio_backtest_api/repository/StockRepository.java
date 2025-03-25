package com.chan.stock_portfolio_backtest_api.repository;

import com.chan.stock_portfolio_backtest_api.domain.Stock;
import com.chan.stock_portfolio_backtest_api.dto.response.StockSearchResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    @Query("SELECT new com.chan.stock_portfolio_backtest_api.dto.response.StockSearchResponseDTO(s.id, s.name, s.shortCode, s.marketCategory) FROM Stock s WHERE s.name LIKE %:query% OR s.shortCode LIKE %:query%")
    List<StockSearchResponseDTO> findByNameOrShortCodeContaining(@Param("query") String query);

    Stock findByName(String query);

    List<Stock> findAllByName(String name);

    List<Stock> findAllByShortCode(String shortCode);

    List<Stock> findAllByIsinCode(String isinCode);
}