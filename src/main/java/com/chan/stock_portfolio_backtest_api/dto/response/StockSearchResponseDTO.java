package com.chan.stock_portfolio_backtest_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockSearchResponseDTO {
    private String name;
    private String shortCode;
    private String marketCategory;
}
