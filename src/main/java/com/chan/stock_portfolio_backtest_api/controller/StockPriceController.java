package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.StockPriceDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.StockPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("api/v1/stock-price")
@Tag(name = "Stock Price", description = "주식 가격 조회 API")
public class StockPriceController {
    private final StockPriceService stockPriceService;

    public StockPriceController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @Operation(summary = "주식 가격 조회", description = "이름 으로 주식 가격 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
            @ApiResponse(responseCode = "404", description = "주식 정보 없음")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<StockPriceDTO>>> getStockByName(
            @RequestParam("name") String name,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<StockPriceDTO> stockPriceDTOList;
        if (startDate != null && endDate != null) {
            stockPriceDTOList = stockPriceService.findStockPricesByStockNameAndDateRange(name, startDate, endDate);
        } else {
            stockPriceDTOList = stockPriceService.findStockPricesByStockName(name);
        }

        ResponseDTO<List<StockPriceDTO>> response = ResponseDTO.<List<StockPriceDTO>>builder()
                .status("success")
                .data(stockPriceDTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
