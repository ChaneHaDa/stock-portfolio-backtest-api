package com.chan.stock_portfolio_backtest_api.repository;

import com.chan.stock_portfolio_backtest_api.domain.IndexPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IndexPriceRepository extends JpaRepository<IndexPrice, Integer> {
    List<IndexPrice> findAllByIndexInfo_Id(Integer stockId);

    @Query("SELECT ip FROM IndexPrice ip WHERE ip.indexInfo.id = :id " +
            "AND ip.baseDate >= :startDate " +
            "AND ip.baseDate <= :endDate")
    List<IndexPrice> findByIndexInfoIdAndDateRange(@Param("id") Integer id,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
}
