package com.chan.stock_portfolio_backtest_api.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chan.stock_portfolio_backtest_api.domain.StockPrice;

public interface StockPriceRepository extends JpaRepository<StockPrice, Integer> {
	@Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :id")
	Page<StockPrice> findByStockIdWithPaging(@Param("id") Integer id, Pageable pageable);

	@Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :id " +
		"AND sp.baseDate >= :startDate " +
		"AND sp.baseDate <= :endDate")
	Page<StockPrice> findByStockIdAndDateRangeWithPaging(@Param("id") Integer id,
		@Param("startDate") LocalDate startDate,
		@Param("endDate") LocalDate endDate,
		Pageable pageable);
}
