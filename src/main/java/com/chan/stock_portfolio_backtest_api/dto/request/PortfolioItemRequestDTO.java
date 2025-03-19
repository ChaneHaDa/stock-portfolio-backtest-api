package com.chan.stock_portfolio_backtest_api.dto.request;

import com.chan.stock_portfolio_backtest_api.domain.PortfolioItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItemRequestDTO {
    private Integer stockId;
    private Float weight;

    public static PortfolioItemRequestDTO entityToDTO(PortfolioItem portfolioItem) {
        return PortfolioItemRequestDTO.builder()
                .stockId(portfolioItem.getStockId())
                .weight(portfolioItem.getWeight())
                .build();
    }

    public static PortfolioItem DTOToEntity(PortfolioItemRequestDTO portfolioItemRequestDTO) {
        return PortfolioItem.builder()
                .stockId(portfolioItemRequestDTO.getStockId())
                .weight(portfolioItemRequestDTO.getWeight())
                .build();
    }

}
