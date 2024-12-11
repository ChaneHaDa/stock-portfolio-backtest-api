package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.db.dto.CalcStockPriceDTO;
import com.chan.stock_portfolio_backtest_api.db.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.db.service.StockService;
import com.chan.stock_portfolio_backtest_api.dto.PortfolionputDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PortfolioService {
    private final StockService stockService;

    public PortfolioService(StockService stockService) {
        this.stockService = stockService;
    }

    public Object getBacktestResult(PortfolionputDTO portfolionputDTO) {
        Map<String, Float> portfolioStock = portfolionputDTO.getPortfolioInputItemDTOList().stream()
                .collect(Collectors.toMap(item -> item.getStockName(), item -> item.getWeight()));

        List<StockDTO> stockDTOList = stockService
                .findStocksByNamesAndDateRange(portfolioStock.keySet().stream().toList(), portfolionputDTO.getStartDate(),
                        portfolionputDTO.getEndDate());

        Map<LocalDate, Float> dateMap = new HashMap<>();
        LocalDate currentDate = portfolionputDTO.getStartDate().withDayOfMonth(1);

        while (!currentDate.isAfter(portfolionputDTO.getEndDate())) {
            dateMap.put(currentDate, 1.0f);
            currentDate = currentDate.plusMonths(1);
        }

        for (StockDTO stockDTO : stockDTOList) {
            List<CalcStockPriceDTO> CalcStockPriceDTOS = stockDTO.getCalcStockPriceList();
            String name = stockDTO.getName();
            for (CalcStockPriceDTO calcStockPriceDTO : CalcStockPriceDTOS) {
                dateMap.put(calcStockPriceDTO.getBaseDate(), dateMap.get(calcStockPriceDTO.getBaseDate()) * calcStockPriceDTO.getMonthlyRor() * portfolioStock.get(name));
            }
        }


        return 0.0f;
    }


}
