package com.chan.stock_portfolio_backtest_api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioInputItemDTO {
    @NotNull(message = "Stock name must not be null")
    private String stockName;

    @Min(value = 0, message = "Weight must be at least 0")
    @Max(value = 1, message = "Weight must be at most 1")
    @NotNull(message = "weight must not be null")
    private float weight;
}
