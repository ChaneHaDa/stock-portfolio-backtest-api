package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.StockResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.StockSearchResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/stocks")
@Tag(name = "Stock API", description = "주식 정보 및 가격 조회 API")
@Validated
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @Operation(summary = "주식 id 조회", description = "쿼리로 주식 id 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
            @ApiResponse(responseCode = "404", description = "주식 정보 없음")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<StockResponseDTO>>> getStocksByParams(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "shortCode", required = false) String shortCode,
            @RequestParam(value = "isinCode", required = false) String isinCode
    ) {
        List<StockResponseDTO> stockResponseDTOList = stockService.findStocksByParams(name, shortCode, isinCode);
        ResponseDTO<List<StockResponseDTO>> response = ResponseDTO.<List<StockResponseDTO>>builder()
                .status("success")
                .data(stockResponseDTOList)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "주식 검색", description = "검색어로 주식 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
            @ApiResponse(responseCode = "404", description = "검색 결과 없음")
    })
    @GetMapping("/search/{query}")
    public ResponseEntity<ResponseDTO<List<StockSearchResponseDTO>>> searchStocks(
            @PathVariable("query")
            @NotBlank(message = "검색어는 필수입니다.")
            @Size(min = 2, message = "검색어는 2자 이상이어야 합니다.") String query
    ) {
        List<StockSearchResponseDTO> stockSearchResponseDTOList = stockService.findStocksByQuery(query.trim());
        ResponseDTO<List<StockSearchResponseDTO>> response = ResponseDTO.<List<StockSearchResponseDTO>>builder()
                .status("success")
                .data(stockSearchResponseDTOList)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 주식 검색", description = "id로 주식 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청값"),
            @ApiResponse(responseCode = "404", description = "주식 정보 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<StockResponseDTO>> getStocksById(
            @PathVariable("id")
            @NotNull(message = "id는 필수입니다.") Integer id
    ) {
        StockResponseDTO stockResponseDTO = stockService.findStockById(id);
        ResponseDTO<StockResponseDTO> response = ResponseDTO.<StockResponseDTO>builder()
                .status("success")
                .data(stockResponseDTO)
                .build();

        return ResponseEntity.ok(response);
    }
}
