package com.chan.stock_portfolio_backtest_api.db.dto;

import com.chan.stock_portfolio_backtest_api.db.entity.StockPrice;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceDTO {
    private Integer id;
    private Integer closePrice;
    private Integer openPrice;
    private Integer lowPrice;
    private Integer highPrice;
    private Integer tradeQuantity;
    private Long tradeAmount;
    private Long issuedCount;
    private LocalDate baseDate;

    public static StockPriceDTO entityToDTO(StockPrice stockPrice) {
        return new StockPriceDTO(stockPrice.getId(), stockPrice.getClosePrice(), stockPrice.getOpenPrice(),
                stockPrice.getLowPrice(), stockPrice.getHighPrice(), stockPrice.getTradeQuantity(),
                stockPrice.getTradeAmount(), stockPrice.getIssuedCount(), stockPrice.getBaseDate());
    }
}
