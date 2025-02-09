package com.chan.stock_portfolio_backtest_api.dto.request;

import com.chan.stock_portfolio_backtest_api.domain.CalcStockPrice;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalcStockPriceRequestDTO {
    private Integer id;
    private Integer price;
    private float monthlyRor;
    private LocalDate baseDate;

    public static CalcStockPriceRequestDTO entityToDTO(CalcStockPrice calcStockPrice) {
        return new CalcStockPriceRequestDTO(calcStockPrice.getId(), calcStockPrice.getPrice(), calcStockPrice.getMonthlyRor(), calcStockPrice.getBaseDate());
    }
}
