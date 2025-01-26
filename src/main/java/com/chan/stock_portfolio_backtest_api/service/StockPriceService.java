package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.dto.request.StockPriceRequestDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.repository.StockPriceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockPriceService {
    private final StockPriceRepository stockPriceRepository;

    public StockPriceService(StockPriceRepository stockPriceRepository) {
        this.stockPriceRepository = stockPriceRepository;
    }

    public List<StockPriceRequestDTO> findStockPricesByStockName(String stockName) {
        List<StockPriceRequestDTO> stockPriceRequestDTOList = stockPriceRepository.findByStockName(stockName)
                .stream()
                .map(StockPriceRequestDTO::entityToDTO)
                .toList();

        if (stockPriceRequestDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("%s not found", stockName));
        }

        return stockPriceRequestDTOList;
    }

    public List<StockPriceRequestDTO> findStockPricesByStockNameAndDateRange(String name, LocalDate startDate, LocalDate endDate) {
        List<StockPriceRequestDTO> stockPriceRequestDTOList = stockPriceRepository.findByStockNameAndDateRange(name, startDate, endDate)
                .stream()
                .map(StockPriceRequestDTO::entityToDTO)
                .toList();

        if (stockPriceRequestDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("%s not found", name));
        }

        return stockPriceRequestDTOList;
    }
}
