package com.chan.stock_portfolio_backtest_api.repository;

import com.chan.stock_portfolio_backtest_api.domain.Portfolio;
import com.chan.stock_portfolio_backtest_api.domain.Users;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
    List<Portfolio> findAllByUser(Users user);
}
