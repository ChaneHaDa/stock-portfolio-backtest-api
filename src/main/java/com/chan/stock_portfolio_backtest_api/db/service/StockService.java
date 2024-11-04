package com.chan.stock_portfolio_backtest_api.db.service;

import com.chan.stock_portfolio_backtest_api.db.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.db.entity.Stock;
import com.chan.stock_portfolio_backtest_api.db.repository.StockRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository){
        this.stockRepository = stockRepository;
    }

    public Stock findStockByQuery(String query) {
        if('0' <= query.charAt(0) && query.charAt(0) <= '9') {
            return stockRepository.findByShortCode(query);
        }else {
            return stockRepository.findByName(query);
        }
    }

    public List<StockDTO> getStocksByQuery(String query) {
        List<StockDTO> stockList = stockRepository.findByNameOrShortCodeContaining(query);
        return stockList;
    }
}
