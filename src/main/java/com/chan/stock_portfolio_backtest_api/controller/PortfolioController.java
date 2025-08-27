package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioBacktestResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioDetailResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.PortfolioBacktestService;
import com.chan.stock_portfolio_backtest_api.service.PortfolioService;
import com.chan.stock_portfolio_backtest_api.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/portfolios")
@Tag(name = "Portfolio API", description = "포트폴리오 백테스트 API")
public class PortfolioController {
    private final PortfolioBacktestService portfolioBacktestService;
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioBacktestService portfolioBacktestService, PortfolioService portfolioService) {
        this.portfolioBacktestService = portfolioBacktestService;
        this.portfolioService = portfolioService;
    }

    @Operation(
            summary = "포트폴리오 백테스팅", 
            description = "포트폴리오 구성과 기간을 입력받아 백테스팅 결과를 계산합니다. 주식 가격 데이터를 기반으로 수익률, 최대 손실 등의 지표를 제공합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", 
                    description = "백테스팅 성공",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"success\",\"data\":{\"totalReturn\":15.5,\"totalReturnRate\":0.155,\"maxDrawdown\":-8.2}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 입력값",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"error\",\"code\":\"VALIDATION_FAILED\",\"message\":\"입력값 검증 실패\",\"data\":{\"startDate\":\"시작일은 필수입니다.\"}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "주식 정보를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"error\",\"code\":\"STOCK_NOT_FOUND\",\"message\":\"요청된 주식을 찾을 수 없습니다.\"}"
                            )
                    )
            )
    })
    @PostMapping("/backtest")
    public ResponseEntity<ResponseDTO<PortfolioBacktestResponseDTO>> createBacktest(
            @RequestBody @Valid PortfolioBacktestRequestDTO portfolioBacktestRequestDTO
    ) {
        PortfolioBacktestResponseDTO result = portfolioBacktestService.calculatePortfolio(portfolioBacktestRequestDTO);
        return ResponseEntity.ok(ResponseUtil.success(result));
    }

    @Operation(summary = "포트폴리오 저장", description = "사용자가 만든 포트폴리오를 저장하고 결과를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "포트폴리오 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값으로 저장 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<ResponseDTO<PortfolioResponseDTO>> savePortfolio(
            @RequestBody @Valid PortfolioRequestDTO portfolioRequestDTO
    ) {
        PortfolioResponseDTO savedPortfolio = portfolioService.createPortfolio(portfolioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.success(savedPortfolio));
    }

    @Operation(summary = "사용자 포트폴리오 리스트 조회", description = "로그인한 사용자가 저장한 모든 포트폴리오 리스트 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포트폴리오 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "포트폴리오가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<ResponseDTO<List<PortfolioResponseDTO>>> getPortfolioById() {
        List<PortfolioResponseDTO> portfolioResponseDTOList = portfolioService.findPortfolioByUser();
        return ResponseEntity.ok(ResponseUtil.success(portfolioResponseDTOList));
    }

    @Operation(summary = "포트폴리오 상세 조회", description = "포트폴리오 및 구성요소 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포트폴리오 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "포트폴리오가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<PortfolioDetailResponseDTO>> getPortfolioDetails(@PathVariable Integer id) {
        PortfolioDetailResponseDTO portfolioDetailResponseDTO = portfolioService.findPortfolioById(id);
        return ResponseEntity.ok(ResponseUtil.success(portfolioDetailResponseDTO));
    }

    @Operation(summary = "포트폴리오 삭제", description = "포트폴리오 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포트폴리오 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "포트폴리오가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deletePortfolio(@PathVariable Integer id) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.ok(ResponseUtil.success("포트폴리오 삭제 성공"));
    }


    @Operation(summary = "포트폴리오 업데이트", description = "포트폴리오 내용을 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포트폴리오 업데이트 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "포트폴리오가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<PortfolioResponseDTO>> updatePortfolio(@PathVariable Integer id, @RequestBody @Valid PortfolioRequestDTO portfolioRequestDTO) {
        PortfolioResponseDTO portfolioResponseDTO = portfolioService.updatePortfolio(id, portfolioRequestDTO);
        return ResponseEntity.ok(ResponseUtil.success(portfolioResponseDTO));
    }

}
