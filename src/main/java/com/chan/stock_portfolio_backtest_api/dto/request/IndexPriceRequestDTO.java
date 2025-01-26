package com.chan.stock_portfolio_backtest_api.dto.request;

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
public class IndexPriceRequestDTO {

    private Integer id;
    private Float closePrice;
    private Float openPrice;
    private Float lowPrice;
    private Float highPrice;
    private Float yearlyDiff;
    private LocalDate baseDate;

    public static IndexPriceRequestDTO entityToDTO(IndexPrice indexPrice) {
        return new IndexPriceRequestDTO(indexPrice.getId(), indexPrice.getClosePrice(), indexPrice.getOpenPrice(),
                indexPrice.getLowPrice(), indexPrice.getHighPrice(), indexPrice.getYearlyDiff(), indexPrice.getBaseDate());
    }

}
