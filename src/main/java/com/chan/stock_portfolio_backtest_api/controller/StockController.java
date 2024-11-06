package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.constants.AppConstants;
import com.chan.stock_portfolio_backtest_api.db.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.db.dto.StockSearchDTO;
import com.chan.stock_portfolio_backtest_api.db.service.StockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Stock",
        description = "get Stock info and Price"
)
@RestController
@RequestMapping("api/v1/stock")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<StockDTO>> getStocksByNamesAndDateRange(@RequestParam("names") List<String> names,
                                                                       @RequestParam(value = "startDate", defaultValue = AppConstants.DEFAULT_START_DATE) String startDate,
                                                                       @RequestParam(value = "endDate", defaultValue = AppConstants.DEFAULT_END_DATE) String endDate) {
        List<StockDTO> stockDTOList = stockService.findStocksByNamesAndDateRange(names, startDate, endDate);

        return ResponseEntity.ok().body(stockDTOList);
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<List<StockSearchDTO>> searchStocks(@PathVariable("query") String query) {
        List<StockSearchDTO> stockSearchDTOList = stockService.findStocksByQuery(query);

        return ResponseEntity.ok().body(stockSearchDTOList);
    }

}
