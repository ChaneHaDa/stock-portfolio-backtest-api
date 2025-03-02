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
        return StockRequestDTO.builder()
                .id(stock.getId())
                .name(stock.getName())
                .shortCode(stock.getShortCode())
                .isinCode(stock.getIsinCode())
                .marketCategory(stock.getMarketCategory())
                .stockPriceList(stock.getStockPriceList().stream().map(StockPriceRequestDTO::entityToDTO).toList())
                .calcStockPriceList(stock.getCalcStockPriceList().stream().map(CalcStockPriceRequestDTO::entityToDTO).toList())
                .build();
    }
}
