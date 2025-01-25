package com.chan.stock_portfolio_backtest_api.dto;

import com.chan.stock_portfolio_backtest_api.domain.CalcIndexPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalcIndexPriceDTO {
    private Integer id;
    private Integer price;
    private float monthlyRor;
    private LocalDate baseDate;

    public static CalcIndexPriceDTO entityToDTO(CalcIndexPrice calcIndexPrice) {
        return new CalcIndexPriceDTO(calcIndexPrice.getId(), calcIndexPrice.getPrice(), calcIndexPrice.getMonthlyRor(), calcIndexPrice.getBaseDate());
    }
}