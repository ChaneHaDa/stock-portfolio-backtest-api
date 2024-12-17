package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.db.dto.CalcStockPriceDTO;
import com.chan.stock_portfolio_backtest_api.db.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.db.service.StockService;
import com.chan.stock_portfolio_backtest_api.dto.PortfolioReturnDTO;
import com.chan.stock_portfolio_backtest_api.dto.PortfolioReturnItemDTO;
import com.chan.stock_portfolio_backtest_api.dto.PortfolionputDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
        Map<String, Float> stockWeightMap = portfolionputDTO.getPortfolioInputItemDTOList().stream()
                .collect(Collectors.toMap(item -> item.getStockName(), item -> item.getWeight()));

        List<StockDTO> stockDTOList = stockService
                .findStocksByNamesAndDateRange(stockWeightMap.keySet().stream().toList(), portfolionputDTO.getStartDate(),
                        portfolionputDTO.getEndDate());

        Map<LocalDate, Float> totalDateRorMap = new HashMap<>();

        LocalDate currentDate = portfolionputDTO.getStartDate().withDayOfMonth(1);
        while (!currentDate.isAfter(portfolionputDTO.getEndDate())) {
            totalDateRorMap.put(currentDate, 0f);
            currentDate = currentDate.plusMonths(1);
        }

        Float totalPortfolioRor = 1f;
        List<PortfolioReturnItemDTO> portfolioReturnItemDTOS = new ArrayList<>();
        for (StockDTO stockDTO : stockDTOList) {
            PortfolioReturnItemDTO portfolioReturnItemDTO = new PortfolioReturnItemDTO();
            String name = stockDTO.getName();
            portfolioReturnItemDTO.setName(name);

            Map<LocalDate, Float> stockDateMap = new HashMap<>();
            List<CalcStockPriceDTO> CalcStockPriceDTOS = stockDTO.getCalcStockPriceList();
            Float totalRorByStock = 1f;
            for (CalcStockPriceDTO calcStockPriceDTO : CalcStockPriceDTOS) {
                Float stockRor = calcStockPriceDTO.getMonthlyRor() * stockWeightMap.get(name);

                totalDateRorMap.put(calcStockPriceDTO.getBaseDate(), totalDateRorMap.get(calcStockPriceDTO.getBaseDate()) + stockRor);
                totalPortfolioRor *= 1 + (stockRor / 100);

                stockDateMap.put(calcStockPriceDTO.getBaseDate(), calcStockPriceDTO.getMonthlyRor());
                totalRorByStock *= 1 + (calcStockPriceDTO.getMonthlyRor() / 100);
            }
            portfolioReturnItemDTO.setTotalRor((totalRorByStock - 1) * 100);
            portfolioReturnItemDTO.setMonthlyRor(stockDateMap);
            portfolioReturnItemDTOS.add(portfolioReturnItemDTO);
        }

        PortfolioReturnDTO portfolioReturnDTO = new PortfolioReturnDTO();
        portfolioReturnDTO.setTotalRor((totalPortfolioRor - 1) * 100);
        portfolioReturnDTO.setPortfolionput(portfolionputDTO);
        portfolioReturnDTO.setMonthlyRor(totalDateRorMap);
        portfolioReturnDTO.setPortfolioReturnItemDTOS(portfolioReturnItemDTOS);

        return portfolioReturnDTO;
    }


}
