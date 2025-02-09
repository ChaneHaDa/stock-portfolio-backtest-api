package com.chan.stock_portfolio_backtest_api.dto.request;

import com.chan.stock_portfolio_backtest_api.domain.IndexPrice;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexPriceRequestDTO {

    private Integer id;
    private Float closePrice;
    private Float openPrice;
    private Float lowPrice;
    private Float highPrice;
    private Float yearlyDiff;
    private LocalDate baseDate;

    public static IndexPriceRequestDTO entityToDTO(IndexPrice indexPrice) {
        return IndexPriceRequestDTO.builder()
                .id(indexPrice.getId())
                .closePrice(indexPrice.getClosePrice())
                .openPrice(indexPrice.getOpenPrice())
                .lowPrice(indexPrice.getLowPrice())
                .highPrice(indexPrice.getHighPrice())
                .yearlyDiff(indexPrice.getYearlyDiff())
                .baseDate(indexPrice.getBaseDate())
                .build();
    }

}
