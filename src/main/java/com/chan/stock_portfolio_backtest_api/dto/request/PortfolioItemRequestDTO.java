package com.chan.stock_portfolio_backtest_api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItemRequestDTO {
    private String name;
    private Float weight;
}
