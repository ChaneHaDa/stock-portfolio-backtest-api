package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.constants.AppConstants;
import com.chan.stock_portfolio_backtest_api.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.dto.StockSearchDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/stock")
@Tag(name = "Stock", description = "주식 정보 및 가격 조회 API")
@Validated
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @Operation(summary = "기간 주식 조회", description = "이름과 기간으로 주식 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
            @ApiResponse(responseCode = "404", description = "주식 정보 없음")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<StockDTO>>> getStocksByNamesAndDateRange(
            @RequestParam("names") @NotEmpty(message = "주식명 목록은 필수입니다.") List<String> names,
            @RequestParam(value = "startDate", defaultValue = AppConstants.DEFAULT_START_DATE)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", defaultValue = AppConstants.DEFAULT_END_DATE)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        List<StockDTO> stockDTOList = stockService.findStocksByNamesAndDateRange(names, startDate, endDate);
        ResponseDTO<List<StockDTO>> response = ResponseDTO.<List<StockDTO>>builder()
                .status("success")
                .data(stockDTOList)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "주식 검색", description = "검색어로 주식 목록 조회")
    @GetMapping("/search/{query}")
    public ResponseEntity<ResponseDTO<List<StockSearchDTO>>> searchStocks(
            @PathVariable("query")
            @NotBlank(message = "검색어는 필수입니다.")
            @Size(min = 2, message = "검색어는 2자 이상이어야 합니다.") String query
    ) {
        List<StockSearchDTO> stockSearchDTOList = stockService.findStocksByQuery(query.trim());
        ResponseDTO<List<StockSearchDTO>> response = ResponseDTO.<List<StockSearchDTO>>builder()
                .status("success")
                .data(stockSearchDTOList)
                .build();

        return ResponseEntity.ok(response);
    }
}
