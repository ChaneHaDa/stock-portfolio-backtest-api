package com.chan.stock_portfolio_backtest_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioReturnDTO {

    private PortfolionputDTO portfolionput;
    private Float totalRor;
    private Map<LocalDate, Float> monthlyRor;
    private List<PortfolioReturnItemDTO> portfolioReturnItemDTOS;
}
