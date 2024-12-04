package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.db.dto.PortfolioStockPriceDTO;
import com.chan.stock_portfolio_backtest_api.db.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.db.dto.StockPriceDTO;
import com.chan.stock_portfolio_backtest_api.db.service.StockService;
import com.chan.stock_portfolio_backtest_api.dto.PortfolioInputItemDTO;
import com.chan.stock_portfolio_backtest_api.dto.PortfolionputDTO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class PortfolioService {
    private final StockService stockService;

    public PortfolioService(StockService stockService) {
        this.stockService = stockService;
    }

    public Float getBacktestResult(PortfolionputDTO portfolionputDTO) {
        List<String> stockNameList = portfolionputDTO.getPortfolioInputItemDTOList()
                .stream()
                .map(PortfolioInputItemDTO::getStockName)
                .toList();

        List<Float> weightList = portfolionputDTO.getPortfolioInputItemDTOList()
                .stream()
                .map(PortfolioInputItemDTO::getWeight)
                .toList();

        List<StockDTO> stockDTOList = stockService
                .findStocksByNamesAndDateRange(stockNameList, portfolionputDTO.getStartDate(),
                        portfolionputDTO.getEndDate());

        Float rorPortfolio = 0f;

        Map<String, Map<LocalDate, Integer>> stockPriceMap = new HashMap<>();
        for (StockDTO stockDTO : stockDTOList) {
            Map<LocalDate, Integer> stockDateMap = stockDTO.getPortfolioStockPriceList().stream().collect(Collectors.toMap(ps->ps.getBaseDate(), ps->ps.getClosePrice()));
            stockPriceMap.put(stockDTO.getName(), stockDateMap);
        }

        System.out.println(stockPriceMap);

        return rorPortfolio;
    }

}
