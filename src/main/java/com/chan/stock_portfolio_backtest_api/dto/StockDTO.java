package com.chan.stock_portfolio_backtest_api.dto;

import com.chan.stock_portfolio_backtest_api.domain.Stock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private List<CalcStockPriceDTO> calcStockPriceList;

    public static StockDTO entityToDTO(Stock stock) {
        return new StockDTO(stock.getId(), stock.getName(), stock.getShortCode(), stock.getIsinCode(),
                stock.getMarketCategory(), stock.getStockPriceList().stream().map(StockPriceDTO::entityToDTO).toList(),
                stock.getCalcStockPriceList().stream().map(CalcStockPriceDTO::entityToDTO).toList());
    }
}
