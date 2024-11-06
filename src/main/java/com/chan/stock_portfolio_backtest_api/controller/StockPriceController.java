package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.constants.AppConstants;
import com.chan.stock_portfolio_backtest_api.db.dto.StockPriceDTO;
import com.chan.stock_portfolio_backtest_api.db.service.StockPriceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Stock Price",
        description = "get Stock Price"
)
@RestController
@RequestMapping("api/v1/stock-price")
public class StockPriceController {
    private final StockPriceService stockPriceService;

    public StockPriceController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @GetMapping
    public ResponseEntity<List<StockPriceDTO>> getStockByName(@RequestParam("name") String name,
                                                              @RequestParam(value = "startDate", defaultValue = AppConstants.DEFAULT_START_DATE) String startDate,
                                                              @RequestParam(value = "endDate", defaultValue = AppConstants.DEFAULT_END_DATE) String endDate) {
        List<StockPriceDTO> stockPriceDTOList;
        if (startDate != null && endDate != null) {
            stockPriceDTOList = stockPriceService.findStockPricesByStockNameAndDateRange(name, startDate, endDate);
        } else {
            stockPriceDTOList = stockPriceService.findStockPricesByStockName(name);
        }

        return ResponseEntity.ok().body(stockPriceDTOList);
    }

}
