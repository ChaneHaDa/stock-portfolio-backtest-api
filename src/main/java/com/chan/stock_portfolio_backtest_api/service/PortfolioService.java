package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.dto.request.CalcStockPriceRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.StockRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseItemDTO;
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

    public PortfolioResponseDTO getBacktestResult(PortfolioRequestDTO portfolioRequestDTO) {
        Map<String, Float> stockWeightMap = portfolioRequestDTO.getPortfolioRequestItemDTOList().stream()
                .collect(Collectors.toMap(item -> item.getStockName(), item -> item.getWeight()));

        List<StockRequestDTO> stockRequestDTOList = stockService
                .findStocksByNamesAndDateRange(stockWeightMap.keySet().stream().toList(), portfolioRequestDTO.getStartDate(),
                        portfolioRequestDTO.getEndDate());

        Map<LocalDate, Float> totalDateRorMap = new HashMap<>();

        LocalDate currentDate = portfolioRequestDTO.getStartDate().withDayOfMonth(1);
        while (!currentDate.isAfter(portfolioRequestDTO.getEndDate())) {
            totalDateRorMap.put(currentDate, 0f);
            currentDate = currentDate.plusMonths(1);
        }

        Float totalPortfolioRor = 1f;
        List<PortfolioResponseItemDTO> portfolioResponseItemDTOS = new ArrayList<>();
        for (StockRequestDTO stockRequestDTO : stockRequestDTOList) {
            PortfolioResponseItemDTO portfolioResponseItemDTO = new PortfolioResponseItemDTO();
            String name = stockRequestDTO.getName();
            portfolioResponseItemDTO.setName(name);

            Map<LocalDate, Float> stockDateMap = new HashMap<>();
            List<CalcStockPriceRequestDTO> calcStockPriceRequestDTOS = stockRequestDTO.getCalcStockPriceList();
            Float totalRorByStock = 1f;
            for (CalcStockPriceRequestDTO calcStockPriceRequestDTO : calcStockPriceRequestDTOS) {
                Float stockRor = calcStockPriceRequestDTO.getMonthlyRor() * stockWeightMap.get(name);

                totalDateRorMap.put(calcStockPriceRequestDTO.getBaseDate(), totalDateRorMap.get(calcStockPriceRequestDTO.getBaseDate()) + stockRor);
                totalPortfolioRor *= 1 + (stockRor / 100);

                stockDateMap.put(calcStockPriceRequestDTO.getBaseDate(), calcStockPriceRequestDTO.getMonthlyRor());
                totalRorByStock *= 1 + (calcStockPriceRequestDTO.getMonthlyRor() / 100);
            }
            portfolioResponseItemDTO.setTotalRor((totalRorByStock - 1) * 100);
            portfolioResponseItemDTO.setMonthlyRor(stockDateMap);
            portfolioResponseItemDTOS.add(portfolioResponseItemDTO);
        }

        PortfolioResponseDTO portfolioResponseDTO = new PortfolioResponseDTO();
        portfolioResponseDTO.setTotalRor((totalPortfolioRor - 1) * 100);
        portfolioResponseDTO.setPortfolionput(portfolioRequestDTO);
        portfolioResponseDTO.setMonthlyRor(totalDateRorMap);
        portfolioResponseDTO.setPortfolioResponseItemDTOS(portfolioResponseItemDTOS);

        return portfolioResponseDTO;
    }

}
