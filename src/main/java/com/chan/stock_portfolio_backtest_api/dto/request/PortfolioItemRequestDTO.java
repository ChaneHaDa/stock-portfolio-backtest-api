package com.chan.stock_portfolio_backtest_api.dto.request;

import com.chan.stock_portfolio_backtest_api.domain.PortfolioItem;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItemRequestDTO {
    private String name;
    private Float weight;

    public static PortfolioItemRequestDTO entityToDTO(PortfolioItem portfolioItem) {
        return PortfolioItemRequestDTO.builder()
                .name(portfolioItem.getName())
                .weight(portfolioItem.getWeight())
                .build();
    }
}
