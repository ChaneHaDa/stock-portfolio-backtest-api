package com.chan.stock_portfolio_backtest_api.dto.response;

import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioResponseDTO {
    private PortfolioRequestDTO portfolioInput;
    private Float totalRor;
    private Map<LocalDate, Float> monthlyRor;
    private List<PortfolioResponseItemDTO> portfolioResponseItemDTOS;
}
