package com.chan.stock_portfolio_backtest_api.db.dto;

import com.chan.stock_portfolio_backtest_api.db.entity.PortfolioStockPrice;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioStockPriceDTO {
    private Integer id;
    private Integer closePrice;
    private LocalDate baseDate;

    public static PortfolioStockPriceDTO entityToDTO(PortfolioStockPrice portfolioStockPrice) {
        return new PortfolioStockPriceDTO(portfolioStockPrice.getId(), portfolioStockPrice.getClosePrice(),
                portfolioStockPrice.getBaseDate());
    }
}
