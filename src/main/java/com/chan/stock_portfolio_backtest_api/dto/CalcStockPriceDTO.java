package com.chan.stock_portfolio_backtest_api.dto;

import com.chan.stock_portfolio_backtest_api.domain.CalcStockPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalcStockPriceDTO {
    private Integer id;
    private Integer price;
    private float monthlyRor;
    private LocalDate baseDate;

    public static CalcStockPriceDTO entityToDTO(CalcStockPrice calcStockPrice) {
        return new CalcStockPriceDTO(calcStockPrice.getId(), calcStockPrice.getPrice(), calcStockPrice.getMonthlyRor(), calcStockPrice.getBaseDate());
    }
}
