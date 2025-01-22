package com.chan.stock_portfolio_backtest_api.dto.input;

import com.chan.stock_portfolio_backtest_api.valid.ValidPortfolioInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidPortfolioInput
public class PortfolioInputDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Start date must not be null")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "End date must not be null")
    private LocalDate endDate;

    @Valid
    private List<PortfolioInputItemDTO> portfolioInputItemDTOList;
}
