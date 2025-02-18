package com.chan.stock_portfolio_backtest_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponseItemDTO {
    private String name;
    private Float totalRor;
    private Map<LocalDate, Float> monthlyRor;
}
