package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.PortfolionputDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/portfolio")
public class PortfolioController {
    @PostMapping
    public String postPortfolio(@RequestBody PortfolionputDTO portfolionputDTO) {
        return "test";
    }
}
