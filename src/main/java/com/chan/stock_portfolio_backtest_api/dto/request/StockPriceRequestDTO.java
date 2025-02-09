package com.chan.stock_portfolio_backtest_api.dto.request;

import com.chan.stock_portfolio_backtest_api.domain.StockPrice;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockPriceRequestDTO {
    private Integer id;
    private Integer closePrice;
    private Integer openPrice;
    private Integer lowPrice;
    private Integer highPrice;
    private Integer tradeQuantity;
    private Long tradeAmount;
    private Long issuedCount;
    private LocalDate baseDate;

    public static StockPriceRequestDTO entityToDTO(StockPrice stockPrice) {
        return new StockPriceRequestDTO(stockPrice.getId(), stockPrice.getClosePrice(), stockPrice.getOpenPrice(),
                stockPrice.getLowPrice(), stockPrice.getHighPrice(), stockPrice.getTradeQuantity(),
                stockPrice.getTradeAmount(), stockPrice.getIssuedCount(), stockPrice.getBaseDate());
    }
}
