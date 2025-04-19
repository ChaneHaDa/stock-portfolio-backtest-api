package com.chan.stock_portfolio_backtest_api.dto.request;

import com.chan.stock_portfolio_backtest_api.domain.CalcIndexPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalcIndexPriceRequestDTO {
    private Integer id;
    private Float price;
    private Float monthlyRor;
    private LocalDate baseDate;

    public static CalcIndexPriceRequestDTO entityToDTO(CalcIndexPrice calcIndexPrice) {
        return CalcIndexPriceRequestDTO.builder()
                .id(calcIndexPrice.getId())
                .price(calcIndexPrice.getPrice())
                .monthlyRor(calcIndexPrice.getMonthlyRor())
                .baseDate(calcIndexPrice.getBaseDate())
                .build();
    }
}