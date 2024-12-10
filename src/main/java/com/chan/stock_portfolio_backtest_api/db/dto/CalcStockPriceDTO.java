package com.chan.stock_portfolio_backtest_api.db.dto;

import com.chan.stock_portfolio_backtest_api.db.entity.CalcStockPrice;
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
    private Integer closePrice;
    private LocalDate baseDate;

    public static CalcStockPriceDTO entityToDTO(CalcStockPrice calcStockPrice) {
        return new CalcStockPriceDTO(calcStockPrice.getId(), calcStockPrice.getClosePrice(),
                calcStockPrice.getBaseDate());
    }
}
