package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.PortfolionputDTO;
import com.chan.stock_portfolio_backtest_api.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    @CrossOrigin
    public ResponseEntity<Object> postPortfolio(@RequestBody @Valid PortfolionputDTO portfolionputDTO) {
        return ResponseEntity.ok().body(portfolioService.getBacktestResult(portfolionputDTO));
    }
}
