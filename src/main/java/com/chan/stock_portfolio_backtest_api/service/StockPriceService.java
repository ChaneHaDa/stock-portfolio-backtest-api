package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.dto.StockPriceDTO;
import com.chan.stock_portfolio_backtest_api.repository.StockPriceRepository;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

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

    public List<StockPriceDTO> findStockPricesByStockNameAndDateRange(String name, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        List<StockPriceDTO> stockPriceDTOList = stockPriceRepository.findByStockNameAndDateRange(name, start, end)
                .stream()
                .map(StockPriceDTO::entityToDTO)
                .toList();

        if (stockPriceDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("%s not found", name));
        }

        return stockPriceDTOList;
    }
}
