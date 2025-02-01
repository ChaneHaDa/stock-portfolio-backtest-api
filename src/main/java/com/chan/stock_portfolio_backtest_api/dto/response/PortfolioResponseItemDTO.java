package com.chan.stock_portfolio_backtest_api.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioResponseItemDTO {
    private String name;
    private Float totalRor;
    private Map<LocalDate, Float> monthlyRor;
}
