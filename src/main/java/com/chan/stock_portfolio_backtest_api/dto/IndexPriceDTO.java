package com.chan.stock_portfolio_backtest_api.dto;

import com.chan.stock_portfolio_backtest_api.domain.IndexPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndexPriceDTO {

    private Integer id;
    private Float closePrice;
    private Float openPrice;
    private Float lowPrice;
    private Float highPrice;
    private Float yearlyDiff;
    private LocalDate baseDate;

    public static IndexPriceDTO entityToDTO(IndexPrice indexPrice) {
        return new IndexPriceDTO(indexPrice.getId(), indexPrice.getClosePrice(), indexPrice.getOpenPrice(),
                indexPrice.getLowPrice(), indexPrice.getHighPrice(), indexPrice.getYearlyDiff(), indexPrice.getBaseDate());
    }

}
