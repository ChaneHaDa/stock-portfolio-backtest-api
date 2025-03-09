package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.dto.response.StockResponseDTO;
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

    public List<StockResponseDTO> findStocksByNamesAndDateRange(List<String> names, LocalDate startDate, LocalDate endDate) {
        List<StockResponseDTO> stockResponseDTOList = stockRepository.findByNameInAndStockPriceDateRange(names, startDate, endDate)
                .stream()
                .map(StockResponseDTO::entityToDTO)
                .toList();

        if (stockResponseDTOList.isEmpty()) {
            throw new EntityNotFoundException(String.format("Stock not found"));
        }

        return stockResponseDTOList;
    }

    public StockResponseDTO findStockById(Integer id) {
        return StockResponseDTO.entityToDTO(stockRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Stock not found")));
    }
}
