package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioBacktestResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.PortfolioBacktestService;
import com.chan.stock_portfolio_backtest_api.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @PostMapping("/backtest")
    @Operation(summary = "포트폴리오 백테스팅", description = "포트폴리오 백테스팅 결과를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "백테스팅 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값")
    })
    public ResponseEntity<ResponseDTO<PortfolioBacktestResponseDTO>> createBacktest(
            @RequestBody @Valid PortfolioBacktestRequestDTO portfolioBacktestRequestDTO
    ) {
        PortfolioBacktestResponseDTO result = portfolioBacktestService.calculatePortfolio(portfolioBacktestRequestDTO);
        ResponseDTO<PortfolioBacktestResponseDTO> response = ResponseDTO.<PortfolioBacktestResponseDTO>builder()
                .status("success")
                .data(result)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "포트폴리오 저장", description = "사용자가 만든 포트폴리오를 저장하고 결과를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "포트폴리오 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값으로 저장 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDTO<PortfolioResponseDTO>> savePortfolio(
            @RequestBody @Valid PortfolioRequestDTO portfolioRequestDTO
    ) {

        PortfolioResponseDTO savedPortfolio = portfolioService.createPortfolio(portfolioRequestDTO);

        ResponseDTO<PortfolioResponseDTO> response = ResponseDTO.<PortfolioResponseDTO>builder()
                .status("success")
                .data(savedPortfolio)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "포트폴리오 조회", description = "로그인한 사용자가 저장한 모든 포트폴리오 리스트 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포트폴리오 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "포트폴리오가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDTO<List<PortfolioResponseDTO>>> getPortfolios() {
        List<PortfolioResponseDTO> portfolioResponseDTOList = portfolioService.findPortfolioByUser();

        ResponseDTO<List<PortfolioResponseDTO>> response = ResponseDTO.<List<PortfolioResponseDTO>>builder()
                .status("success")
                .data(portfolioResponseDTOList)
                .build();

        return ResponseEntity.ok(response);
    }

}
