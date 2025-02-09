package com.chan.stock_portfolio_backtest_api.dto.request;

import com.chan.stock_portfolio_backtest_api.domain.Stock;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockRequestDTO {
    private Integer id;
    private String name;
    private String shortCode;
    private String isinCode;
    private String marketCategory;
    private List<StockPriceRequestDTO> stockPriceList;
    private List<CalcStockPriceRequestDTO> calcStockPriceList;

    public static StockRequestDTO entityToDTO(Stock stock) {
        return new StockRequestDTO(stock.getId(), stock.getName(), stock.getShortCode(), stock.getIsinCode(),
                stock.getMarketCategory(), stock.getStockPriceList().stream().map(StockPriceRequestDTO::entityToDTO).toList(),
                stock.getCalcStockPriceList().stream().map(CalcStockPriceRequestDTO::entityToDTO).toList());
    }
}
