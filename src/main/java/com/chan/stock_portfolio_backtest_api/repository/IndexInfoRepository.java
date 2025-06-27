package com.chan.stock_portfolio_backtest_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chan.stock_portfolio_backtest_api.domain.IndexInfo;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Integer> {
	Optional<IndexInfo> findByName(String name);
}
