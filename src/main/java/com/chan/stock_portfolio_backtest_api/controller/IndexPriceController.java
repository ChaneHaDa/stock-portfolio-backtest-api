package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.IndexInfoDTO;
import com.chan.stock_portfolio_backtest_api.dto.IndexPriceDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.IndexInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/index")
@Tag(name = "Index API", description = "지수 정보 조회 API")
public class IndexPriceController {
    private final IndexInfoService indexInfoService;

    public IndexPriceController(IndexInfoService indexInfoService) {
        this.indexInfoService = indexInfoService;
    }

    @Operation(summary = "지수 기본 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "지수 정보 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @GetMapping("/{name}")
    public ResponseEntity<ResponseDTO<IndexInfoDTO>> getIndex(
            @PathVariable("name")
            @NotBlank(message = "지수명은 필수 입력값입니다.") String name
    ) {
        IndexInfoDTO indexInfoDTO = indexInfoService.findIndexInfoByName(name);
        ResponseDTO<IndexInfoDTO> response = ResponseDTO.<IndexInfoDTO>builder()
                .status("success")
                .data(indexInfoDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지수 최종 가격 조회")
    @GetMapping("/{name}/price")
    public ResponseEntity<ResponseDTO<IndexPriceDTO>> getLastIndexPrice(
            @PathVariable("name")
            @NotBlank(message = "지수명은 필수 입력값입니다.") String name
    ) {
        IndexPriceDTO indexPriceDTO = indexInfoService.findLastIndexPriceByName(name);
        ResponseDTO<IndexPriceDTO> response = ResponseDTO.<IndexPriceDTO>builder()
                .status("success")
                .data(indexPriceDTO)
                .build();
        return ResponseEntity.ok(response);
    }

}
