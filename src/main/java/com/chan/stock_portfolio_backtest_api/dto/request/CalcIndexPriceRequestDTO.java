package com.chan.stock_portfolio_backtest_api.dto.request;

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
public class CalcIndexPriceRequestDTO {
    private Integer id;
    private Integer price;
    private float monthlyRor;
    private LocalDate baseDate;

    public static CalcIndexPriceRequestDTO entityToDTO(CalcIndexPrice calcIndexPrice) {
        return new CalcIndexPriceRequestDTO(calcIndexPrice.getId(), calcIndexPrice.getPrice(), calcIndexPrice.getMonthlyRor(), calcIndexPrice.getBaseDate());
    }
}