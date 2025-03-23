package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.domain.Portfolio;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import com.chan.stock_portfolio_backtest_api.domain.Users;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioItemRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioDetailResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.repository.PortfolioRepository;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AuthService authService;
    private final StockRepository stockRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, AuthService authService, StockRepository stockRepository) {
        this.portfolioRepository = portfolioRepository;
        this.authService = authService;
        this.stockRepository = stockRepository;
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

        portfolioRequestDTO.getPortfolioItemRequestDTOList().forEach(item -> {
            Stock stock = stockRepository.findById(item.getStockId()).orElseThrow(EntityNotFoundException::new);
            portfolio.addPortfolioItem(PortfolioItemRequestDTO.DTOToEntity(item, stock));
        });

        return PortfolioResponseDTO.entityToDTO(portfolioRepository.save(portfolio));
    }

    public List<PortfolioResponseDTO> findPortfolioByUser() {
        Users user = authService.getCurrentUser();
        List<PortfolioResponseDTO> portfolioResponseDTOList = portfolioRepository.findAllByUser(user)
                .stream()
                .map(PortfolioResponseDTO::entityToDTO)
                .toList();

        if (portfolioResponseDTOList.isEmpty()) {
            throw new EntityNotFoundException("Portfolio Not Found");
        }

        return portfolioResponseDTOList;
    }

    public PortfolioDetailResponseDTO findPortfolioById(Integer id) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        
        return PortfolioDetailResponseDTO.entityToDTO(portfolio);
    }
}
