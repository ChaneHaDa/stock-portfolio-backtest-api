package com.chan.stock_portfolio_backtest_api.db.service;

import com.chan.stock_portfolio_backtest_api.db.dto.StockPriceDTO;
import com.chan.stock_portfolio_backtest_api.db.repository.StockPriceRepository;
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
        return stockPriceRepository.findByStockName(stockName)
                .stream().map(StockPriceDTO::entityToDTO).toList();
    }

    public List<StockPriceDTO> findStockPricesByStockNameAndDateRange(String name, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        return stockPriceRepository.findByStockNameAndDateRange(name, start, end)
                .stream().map(StockPriceDTO::entityToDTO).toList();
    }
}
