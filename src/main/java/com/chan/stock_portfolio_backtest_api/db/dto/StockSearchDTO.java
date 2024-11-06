package com.chan.stock_portfolio_backtest_api.db.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockSearchDTO {
    private String name;
    private String shortCode;
    private String marketCategory;
}
