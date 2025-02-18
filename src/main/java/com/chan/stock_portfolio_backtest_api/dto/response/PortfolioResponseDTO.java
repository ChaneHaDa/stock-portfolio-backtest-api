package com.chan.stock_portfolio_backtest_api.dto.response;

import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
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
public class PortfolioResponseDTO {

    private PortfolioRequestDTO portfolionput;
    private Float totalRor;
    private Map<LocalDate, Float> monthlyRor;
    private List<PortfolioResponseItemDTO> portfolioResponseItemDTOS;
}
