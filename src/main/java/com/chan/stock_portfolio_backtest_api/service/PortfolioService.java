package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.domain.Portfolio;
import com.chan.stock_portfolio_backtest_api.domain.Users;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseDTO;
import com.chan.stock_portfolio_backtest_api.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AuthService authService;

    public PortfolioService(PortfolioRepository portfolioRepository, AuthService authService) {
        this.portfolioRepository = portfolioRepository;
        this.authService = authService;
    }

    public PortfolioResponseDTO createPortfolio(PortfolioRequestDTO portfolioRequestDTO) {
        Users user = authService.getCurrentUser();
        Portfolio portfolio = Portfolio.builder()
                .name(portfolioRequestDTO.getName())
                .description(portfolioRequestDTO.getDescription())
                .ror(portfolioRequestDTO.getRor())
                .amount(portfolioRequestDTO.getAmount())
                .price(portfolioRequestDTO.getPrice())
                .startDate(portfolioRequestDTO.getStartDate())
                .endDate(portfolioRequestDTO.getEndDate())
                .volatility(portfolioRequestDTO.getVolatility())
                .user(user)
                .build();
        
        return PortfolioResponseDTO.entityToDTO(portfolioRepository.save(portfolio));
    }
}
