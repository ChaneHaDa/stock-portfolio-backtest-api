package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.request.IndexBacktestRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.IndexBacktestResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.IndexInfoResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.IndexPriceResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.IndexBacktestService;
import com.chan.stock_portfolio_backtest_api.service.IndexInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/index")
@Tag(name = "Index API", description = "지수 정보 조회 API")
public class IndexController {
    private final IndexInfoService indexInfoService;
    private final IndexBacktestService indexBacktestService;

    public IndexController(IndexInfoService indexInfoService, IndexBacktestService indexBacktestService) {
        this.indexInfoService = indexInfoService;
        this.indexBacktestService = indexBacktestService;
    }

    @Operation(summary = "특정 지수 기본 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "지수 정보 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<IndexInfoResponseDTO>> getIndex(
            @PathVariable("id")
            @NotNull(message = "id는 필수 입력값입니다.") Integer id
    ) {
        IndexInfoResponseDTO indexInfoResponseDTO = indexInfoService.findIndexInfoById(id);
        ResponseDTO<IndexInfoResponseDTO> response = ResponseDTO.<IndexInfoResponseDTO>builder()
                .status("success")
                .data(indexInfoResponseDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 지수 마지막 가격 조회")
    @GetMapping("/{id}/price/last")
    public ResponseEntity<ResponseDTO<IndexPriceResponseDTO>> getLastIndexPrice(
            @PathVariable("id")
            @NotNull(message = "id는 필수 입력값입니다.") Integer id
    ) {
        IndexPriceResponseDTO indexPriceResponseDTO = indexInfoService.findLastIndexPriceById(id);
        ResponseDTO<IndexPriceResponseDTO> response = ResponseDTO.<IndexPriceResponseDTO>builder()
                .status("success")
                .data(indexPriceResponseDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지수 기반 백테스트")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "백테스트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "지수 정보 없음")
    })
    @GetMapping("/{id}/portfolio")
    public ResponseEntity<ResponseDTO<IndexBacktestResponseDTO>> getIndexPortfolio(
            @PathVariable("id")
            @NotNull(message = "id는 필수 입력값입니다.") Integer id,
            @ModelAttribute @Valid IndexBacktestRequestDTO indexBacktestRequestDTO
    ) {
        IndexInfoResponseDTO indexPriceResponseDTO = indexInfoService.findIndexInfoById(id);

        IndexBacktestResponseDTO backtestResult = indexBacktestService.calculateIndexBacktest(indexBacktestRequestDTO);

        ResponseDTO<IndexBacktestResponseDTO> response = ResponseDTO.<IndexBacktestResponseDTO>builder()
                .status("success")
                .data(backtestResult)
                .build();

        return ResponseEntity.ok(response);
    }

}
