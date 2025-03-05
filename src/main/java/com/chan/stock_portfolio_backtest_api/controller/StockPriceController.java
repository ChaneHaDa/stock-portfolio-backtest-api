package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.request.StockPriceRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.StockPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("api/v1/stocks/{stock_id}/prices")
@Tag(name = "Stock Price API", description = "주식 가격 조회 API")
public class StockPriceController {
    private final StockPriceService stockPriceService;

    public StockPriceController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @Operation(summary = "주식 가격 조회", description = "stock id로 주식 가격 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
            @ApiResponse(responseCode = "404", description = "주식 정보 없음")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<StockPriceRequestDTO>>> getStockByName(
            @PathVariable("stock_id") Integer stockId,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<StockPriceRequestDTO> stockPriceRequestDTOList;
        if (startDate != null && endDate != null) {
            stockPriceRequestDTOList = stockPriceService.findStockPricesByStockIdAndDateRange(stockId, startDate, endDate);
        } else {
            stockPriceRequestDTOList = stockPriceService.findStockPricesByStockId(stockId);
        }

        ResponseDTO<List<StockPriceRequestDTO>> response = ResponseDTO.<List<StockPriceRequestDTO>>builder()
                .status("success")
                .data(stockPriceRequestDTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
