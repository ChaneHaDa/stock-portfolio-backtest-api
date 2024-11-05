package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.db.entity.StockPrice;
import com.chan.stock_portfolio_backtest_api.db.service.StockPriceService;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/stock-price")
public class StockPriceController {
    private final StockPriceService stockPriceService;

    public StockPriceController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @GetMapping
    public ResponseEntity<List<StockPrice>> getStockByName(@RequestParam("name") String name,
                                                           @RequestParam(value = "startDate", required = false) String startDate,
                                                           @RequestParam(value = "endDate", required = false) String endDate) {
        List<StockPrice> stockPriceList;
        if (startDate != null && endDate != null) {
            stockPriceList = stockPriceService.findStockPricesByStockNameAndDateRange(name, startDate, endDate);
        } else {
            stockPriceList = stockPriceService.findStockPricesByStockName(name);
        }
        if (stockPriceList.isEmpty()) {
            throw new EntityNotFoundException(String.format("Stock is not founded"));
        }
        return ResponseEntity.ok().body(stockPriceList);
    }

}
