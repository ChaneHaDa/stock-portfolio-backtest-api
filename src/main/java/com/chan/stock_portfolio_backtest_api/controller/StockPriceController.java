package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.request.StockPriceRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.StockPriceResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.StockPriceService;
import com.chan.stock_portfolio_backtest_api.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(
            summary = "주식 가격 조회", 
            description = "주식 ID로 해당 주식의 가격 정보를 조회합니다. 기간 설정시 해당 기간의 가격 데이터를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", 
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"success\",\"data\":[{\"id\":1,\"closePrice\":50000,\"openPrice\":49500,\"lowPrice\":49000,\"highPrice\":50500,\"tradeQuantity\":12345,\"tradeAmount\":617250000,\"issuedCount\":1234567890,\"baseDate\":\"2024-01-01\"}]}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "주식 정보 없음",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"error\",\"code\":\"ENTITY_NOT_FOUND\",\"message\":\"1 not found\"}"
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<StockPriceResponseDTO>>> getStockByName(
            @PathVariable("stock_id") Integer stockId,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<StockPriceResponseDTO> stockPriceResponseDTOList;
        if (startDate != null && endDate != null) {
            stockPriceResponseDTOList = stockPriceService.findStockPricesByStockIdAndDateRange(stockId, startDate, endDate);
        } else {
            stockPriceResponseDTOList = stockPriceService.findStockPricesByStockId(stockId);
        }

        return ResponseEntity.ok(ResponseUtil.success(stockPriceResponseDTOList));
    }

}
