package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.dto.StockPriceDTO;
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

    public List<StockPriceDTO> findStockPricesByStockName(String stockName) {
        List<StockPriceDTO> stockPriceDTOList = stockPriceRepository.findByStockName(stockName)
                .stream()
                .map(StockPriceDTO::entityToDTO)
                .toList();

        if (stockPriceDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("%s not found", stockName));
        }

        return stockPriceDTOList;
    }

    public List<StockPriceDTO> findStockPricesByStockNameAndDateRange(String name, LocalDate startDate, LocalDate endDate) {
        List<StockPriceDTO> stockPriceDTOList = stockPriceRepository.findByStockNameAndDateRange(name, startDate, endDate)
                .stream()
                .map(StockPriceDTO::entityToDTO)
                .toList();

        if (stockPriceDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("%s not found", name));
        }

        return stockPriceDTOList;
    }
}
