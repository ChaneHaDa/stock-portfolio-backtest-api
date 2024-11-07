package com.chan.stock_portfolio_backtest_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioInputItemDTO {

    private String stockName;
    private float weight;
}
