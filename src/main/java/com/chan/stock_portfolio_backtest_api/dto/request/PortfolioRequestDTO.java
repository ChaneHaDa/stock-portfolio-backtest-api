package com.chan.stock_portfolio_backtest_api.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioRequestDTO {
    private String name;
    private String description;
    private Long amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Float ror;
    private Float volatility;
    private Float price;
    private List<PortfolioItemRequestDTO> portfolioItemRequestDTOList;
}