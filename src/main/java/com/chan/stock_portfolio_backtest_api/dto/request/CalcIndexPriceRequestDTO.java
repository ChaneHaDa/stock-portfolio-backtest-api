package com.chan.stock_portfolio_backtest_api.dto.request;

import com.chan.stock_portfolio_backtest_api.domain.CalcIndexPrice;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalcIndexPriceRequestDTO {
    private Integer id;
    private Integer price;
    private float monthlyRor;
    private LocalDate baseDate;

    public static CalcIndexPriceRequestDTO entityToDTO(CalcIndexPrice calcIndexPrice) {
        return new CalcIndexPriceRequestDTO(calcIndexPrice.getId(), calcIndexPrice.getPrice(), calcIndexPrice.getMonthlyRor(), calcIndexPrice.getBaseDate());
    }
}