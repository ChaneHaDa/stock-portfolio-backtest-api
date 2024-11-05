package com.chan.stock_portfolio_backtest_api.db.service;

import com.chan.stock_portfolio_backtest_api.db.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.db.dto.StockSearchDTO;
import com.chan.stock_portfolio_backtest_api.db.entity.Stock;
import com.chan.stock_portfolio_backtest_api.db.repository.StockRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Stock findStockByQuery(String query) {
        if ('0' <= query.charAt(0) && query.charAt(0) <= '9') {
            return stockRepository.findByShortCode(query);
        } else {
            return stockRepository.findByName(query);
        }
    }

    public List<StockSearchDTO> findStocksByQuery(String query) {
        List<StockSearchDTO> stockSearchDTOS = stockRepository.findByNameOrShortCodeContaining(query);
        return stockSearchDTOS;
    }

    public List<StockDTO> findStocksByNamesAndDateRange(List<String> names, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        return stockRepository.findByNameInAndStockPriceDateRange(names, start, end)
                .stream().map(StockDTO::entityToDTO).toList();
    }

}
