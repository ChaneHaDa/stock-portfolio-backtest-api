package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.db.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.db.entity.Stock;
import com.chan.stock_portfolio_backtest_api.db.service.StockService;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/stock")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<Stock> getStockByQuery(@RequestParam("query") String query) {
        Stock stock1 = stockService.findStockByQuery(query);
        if(stock1 == null) {
            throw new EntityNotFoundException(String.format("%s is not founded", query));
        }
        return ResponseEntity.ok().body(stock1);
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<List<StockDTO>> searchStocks(@PathVariable("query") String query){
        List<StockDTO> stockList = stockService.getStocksByQuery(query);
        if(stockList.isEmpty()){
            throw new EntityNotFoundException(String.format("Stock is not founded"));
        }
        return ResponseEntity.ok().body(stockList);
    }

}
