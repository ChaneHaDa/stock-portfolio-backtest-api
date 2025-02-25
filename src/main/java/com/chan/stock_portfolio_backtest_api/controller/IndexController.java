package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.request.IndexBacktestRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.IndexInfoRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.IndexPriceRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.IndexBacktestResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.IndexBacktestService;
import com.chan.stock_portfolio_backtest_api.service.IndexInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    @Operation(summary = "지수 기본 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "지수 정보 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @GetMapping("/{name}")
    public ResponseEntity<ResponseDTO<IndexInfoRequestDTO>> getIndex(
            @PathVariable("name")
            @NotBlank(message = "지수명은 필수 입력값입니다.") String name
    ) {
        IndexInfoRequestDTO indexInfoRequestDTO = indexInfoService.findIndexInfoByName(name);
        ResponseDTO<IndexInfoRequestDTO> response = ResponseDTO.<IndexInfoRequestDTO>builder()
                .status("success")
                .data(indexInfoRequestDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지수 최종 가격 조회")
    @GetMapping("/{name}/price")
    public ResponseEntity<ResponseDTO<IndexPriceRequestDTO>> getLastIndexPrice(
            @PathVariable("name")
            @NotBlank(message = "지수명은 필수 입력값입니다.") String name
    ) {
        IndexPriceRequestDTO indexPriceRequestDTO = indexInfoService.findLastIndexPriceByName(name);
        ResponseDTO<IndexPriceRequestDTO> response = ResponseDTO.<IndexPriceRequestDTO>builder()
                .status("success")
                .data(indexPriceRequestDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지수 기반 백테스트")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "백테스트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "지수 정보 없음")
    })
    @GetMapping("/{name}/portfolio")
    public ResponseEntity<ResponseDTO<IndexBacktestResponseDTO>> getIndexPortfolio(
            @PathVariable("name")
            @NotBlank(message = "지수명은 필수 입력값입니다.") String name,
            @ModelAttribute @Valid IndexBacktestRequestDTO indexBacktestRequestDTO
    ) {
        indexBacktestRequestDTO.setName(name);
        IndexBacktestResponseDTO backtestResult = indexBacktestService.calculateIndexBacktest(indexBacktestRequestDTO);

        ResponseDTO<IndexBacktestResponseDTO> response = ResponseDTO.<IndexBacktestResponseDTO>builder()
                .status("success")
                .data(backtestResult)
                .build();

        return ResponseEntity.ok(response);
    }

}
