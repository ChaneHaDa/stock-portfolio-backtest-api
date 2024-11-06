package com.chan.stock_portfolio_backtest_api.db.service;

import com.chan.stock_portfolio_backtest_api.db.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.db.dto.StockSearchDTO;
import com.chan.stock_portfolio_backtest_api.db.repository.StockRepository;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
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

    public List<StockSearchDTO> findStocksByQuery(String query) {
        List<StockSearchDTO> stockSearchDTOList = stockRepository.findByNameOrShortCodeContaining(query);

        if (stockSearchDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("Stock not found"));
        }

        return stockSearchDTOList;
    }

    public List<StockDTO> findStocksByNamesAndDateRange(List<String> names, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        List<StockDTO> stockDTOList = stockRepository.findByNameInAndStockPriceDateRange(names, start, end)
                .stream()
                .map(StockDTO::entityToDTO)
                .toList();

        if (stockDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("Stock not found"));
        }

        return stockDTOList;
    }

}
