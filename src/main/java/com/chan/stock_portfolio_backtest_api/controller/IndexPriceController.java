package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.response.IndexPriceResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.IndexPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/indexs/{index_id}/prices")
@Tag(name = "Index Price API", description = "지수 가격 정보 조회 API")
public class IndexPriceController {
    private final IndexPriceService indexPriceService;

    public IndexPriceController(IndexPriceService indexPriceService) {
        this.indexPriceService = indexPriceService;
    }

    @GetMapping
    @Operation(
            summary = "지수 가격 조회",
            description = "특정 지수(Index)의 ID를 기반으로 해당 지수의 가격 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지수 가격 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터 (index_id가 누락되었거나 잘못됨)"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 지수를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDTO<List<IndexPriceResponseDTO>>> getIndexPriceByIndexId(
            @PathVariable(name = "index_id")
            @NotNull(message = "index_id는 필수입니다.") Integer indexId,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        List<IndexPriceResponseDTO> indexPriceResponseDTOList;

        if (startDate != null && endDate != null) {
            indexPriceResponseDTOList = indexPriceService.findIndexPriceByIndexInfoIdAndDateRange(indexId, startDate, endDate);
        } else {
            indexPriceResponseDTOList = indexPriceService.findIndexPriceByIndexId(indexId);
        }

        ResponseDTO<List<IndexPriceResponseDTO>> response = ResponseDTO.<List<IndexPriceResponseDTO>>builder()
                .status("success")
                .data(indexPriceResponseDTOList)
                .build();

        return ResponseEntity.ok(response);
    }
}
