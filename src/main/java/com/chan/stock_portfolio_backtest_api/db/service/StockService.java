package com.chan.stock_portfolio_backtest_api.db.service;

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

    public List<Stock> findAllStock() {
        return stockRepository.findAll();
    }

    public Stock findStockById(Integer id) {
        return stockRepository.findById(id).orElse(null);
    }

}
