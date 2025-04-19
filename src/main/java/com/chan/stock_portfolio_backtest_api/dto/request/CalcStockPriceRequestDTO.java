package com.chan.stock_portfolio_backtest_api.dto.request;

import com.chan.stock_portfolio_backtest_api.domain.CalcStockPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalcStockPriceRequestDTO {
    private Integer id;
    private Float price;
    private Float monthlyRor;
    private LocalDate baseDate;

    public static CalcStockPriceRequestDTO entityToDTO(CalcStockPrice calcStockPrice) {
        return CalcStockPriceRequestDTO.builder()
                .id(calcStockPrice.getId())
                .price(calcStockPrice.getPrice())
                .monthlyRor(calcStockPrice.getMonthlyRor())
                .baseDate(calcStockPrice.getBaseDate())
                .build();
    }
}
