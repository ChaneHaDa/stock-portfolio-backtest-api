package com.chan.stock_portfolio_backtest_api.dto.response;

import com.chan.stock_portfolio_backtest_api.domain.Stock;
import com.chan.stock_portfolio_backtest_api.dto.request.CalcStockPriceRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.StockPriceRequestDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponseDTO {
    private Integer id;
    private String name;
    private String shortCode;
    private String isinCode;
    private String marketCategory;
    private List<StockPriceRequestDTO> stockPriceList;
    private List<CalcStockPriceRequestDTO> calcStockPriceList;

    public static StockResponseDTO entityToDTO(Stock stock) {
        return StockResponseDTO.builder()
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
