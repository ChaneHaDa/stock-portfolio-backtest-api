package com.chan.stock_portfolio_backtest_api.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSearchResponseDTO {
    private String name;
    private String shortCode;
    private String marketCategory;
}
