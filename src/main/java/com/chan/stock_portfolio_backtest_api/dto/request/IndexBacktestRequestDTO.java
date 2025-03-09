package com.chan.stock_portfolio_backtest_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexBacktestRequestDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Start date must not be null")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "End date must not be null")
    private LocalDate endDate;
}
