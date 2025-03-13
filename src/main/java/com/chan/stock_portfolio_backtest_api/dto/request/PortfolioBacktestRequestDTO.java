package com.chan.stock_portfolio_backtest_api.dto.request;

import com.chan.stock_portfolio_backtest_api.valid.ValidPortfolioInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ValidPortfolioInput
@Builder
public class PortfolioBacktestRequestDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Start date must not be null")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "End date must not be null")
    private LocalDate endDate;

    @Builder.Default
    private Long amount = 100000L;

    @Valid
    private List<PortfolioBacktestRequestItemDTO> portfolioBacktestRequestItemDTOList;
}
