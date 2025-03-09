package com.chan.stock_portfolio_backtest_api.repository;

import com.chan.stock_portfolio_backtest_api.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
