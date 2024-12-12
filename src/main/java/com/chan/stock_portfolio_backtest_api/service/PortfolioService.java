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
        Map<String, Float> portfolioStock = portfolionputDTO.getPortfolioInputItemDTOList().stream()
                .collect(Collectors.toMap(item -> item.getStockName(), item -> item.getWeight()));

        List<StockDTO> stockDTOList = stockService
                .findStocksByNamesAndDateRange(portfolioStock.keySet().stream().toList(), portfolionputDTO.getStartDate(),
                        portfolionputDTO.getEndDate());

        Map<LocalDate, Float> dateMap = new HashMap<>();
        LocalDate currentDate = portfolionputDTO.getStartDate().withDayOfMonth(1);

        while (!currentDate.isAfter(portfolionputDTO.getEndDate())) {
            dateMap.put(currentDate, 0f);
            currentDate = currentDate.plusMonths(1);
        }

        List<PortfolioReturnItemDTO> portfolioReturnItemDTOS = new ArrayList<>();

        Float totalRor = 1f;

        for (StockDTO stockDTO : stockDTOList) {
            List<CalcStockPriceDTO> CalcStockPriceDTOS = stockDTO.getCalcStockPriceList();
            PortfolioReturnItemDTO portfolioReturnItemDTO = new PortfolioReturnItemDTO();
            String name = stockDTO.getName();
            portfolioReturnItemDTO.setName(name);
            Map<LocalDate, Float> dateMapByStock = new HashMap<>();
            Float totalRorByStock = 1f;
            for (CalcStockPriceDTO calcStockPriceDTO : CalcStockPriceDTOS) {
                Float ror = calcStockPriceDTO.getMonthlyRor() * portfolioStock.get(name);
                dateMap.put(calcStockPriceDTO.getBaseDate(), dateMap.get(calcStockPriceDTO.getBaseDate()) + ror);
                dateMapByStock.put(calcStockPriceDTO.getBaseDate(), calcStockPriceDTO.getMonthlyRor());
                totalRorByStock += 1 + (calcStockPriceDTO.getMonthlyRor() / 100);
                totalRor *= 1 + (ror / 100);
            }
            portfolioReturnItemDTO.setTotalRor((totalRorByStock - 1) * 100);
            portfolioReturnItemDTO.setMonthlyRor(dateMapByStock);
            portfolioReturnItemDTOS.add(portfolioReturnItemDTO);
        }

        PortfolioReturnDTO portfolioReturnDTO = new PortfolioReturnDTO();
        portfolioReturnDTO.setTotalRor((totalRor - 1) * 100);
        portfolioReturnDTO.setPortfolionput(portfolionputDTO);
        portfolioReturnDTO.setMonthlyRor(dateMap);
        portfolioReturnDTO.setPortfolioReturnItemDTOS(portfolioReturnItemDTOS);

        return portfolioReturnDTO;
    }


}
