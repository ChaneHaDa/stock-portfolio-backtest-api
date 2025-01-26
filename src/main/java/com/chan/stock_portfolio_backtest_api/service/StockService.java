package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.dto.request.StockRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.StockSearchResponseDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<StockSearchResponseDTO> findStocksByQuery(String query) {
        List<StockSearchResponseDTO> stockSearchResponseDTOList = stockRepository.findByNameOrShortCodeContaining(query);

        if (stockSearchResponseDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("Stock not found"));
        }

        return stockSearchResponseDTOList;
    }

    public List<StockRequestDTO> findStocksByNamesAndDateRange(List<String> names, LocalDate startDate, LocalDate endDate) {
        List<StockRequestDTO> stockRequestDTOList = stockRepository.findByNameInAndStockPriceDateRange(names, startDate, endDate)
                .stream()
                .map(StockRequestDTO::entityToDTO)
                .toList();

        if (stockRequestDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("Stock not found"));
        }

        return stockRequestDTOList;
    }

}
