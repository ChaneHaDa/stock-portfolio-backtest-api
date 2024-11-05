package com.chan.stock_portfolio_backtest_api.db.dto;

import com.chan.stock_portfolio_backtest_api.db.entity.Stock;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private Integer id;
    private String name;
    private String shortCode;
    private String isinCode;
    private String marketCategory;
    private List<StockPriceDTO> stockPriceList;

    public static StockDTO entityToDTO(Stock stock) {
        return new StockDTO(stock.getId(), stock.getName(), stock.getShortCode(), stock.getIsinCode(),
                stock.getMarketCategory(), stock.getStockPriceList().stream().map(StockPriceDTO::entityToDTO).toList());
    }
}
