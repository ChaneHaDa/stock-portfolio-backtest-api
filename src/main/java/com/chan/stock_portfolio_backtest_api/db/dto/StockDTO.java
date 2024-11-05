package com.chan.stock_portfolio_backtest_api.db.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private Integer id;
    private String name;
    private String shortCode;
    private String isinCode;
    private String marketCategory;
}
