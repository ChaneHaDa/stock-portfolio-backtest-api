package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.dto.StockSearchDTO;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<StockSearchDTO> findStocksByQuery(String query) {
        List<StockSearchDTO> stockSearchDTOList = stockRepository.findByNameOrShortCodeContaining(query);

        if (stockSearchDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("Stock not found"));
        }

        return stockSearchDTOList;
    }

    public List<StockDTO> findStocksByNamesAndDateRange(List<String> names, LocalDate startDate, LocalDate endDate) {
        List<StockDTO> stockDTOList = stockRepository.findByNameInAndStockPriceDateRange(names, startDate, endDate)
                .stream()
                .map(StockDTO::entityToDTO)
                .toList();

        if (stockDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("Stock not found"));
        }

        return stockDTOList;
    }

}
