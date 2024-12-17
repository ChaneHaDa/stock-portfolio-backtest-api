package com.chan.stock_portfolio_backtest_api.db.repository;

import com.chan.stock_portfolio_backtest_api.db.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Integer> {
    IndexInfo findByName(String name);
}
