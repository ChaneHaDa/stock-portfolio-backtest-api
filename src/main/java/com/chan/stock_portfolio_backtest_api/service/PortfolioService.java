package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.db.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.db.service.StockService;
import com.chan.stock_portfolio_backtest_api.dto.PortfolionputDTO;
import org.springframework.stereotype.Service;

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
        Float rorPortfolio = 0f;

        Map<String, Float> portfolioStock = portfolionputDTO.getPortfolioInputItemDTOList().stream()
                .collect(Collectors.toMap(item -> item.getStockName(), item -> item.getWeight()));

        List<StockDTO> stockDTOList = stockService
                .findStocksByNamesAndDateRange(portfolioStock.keySet().stream().toList(), portfolionputDTO.getStartDate(),
                        portfolionputDTO.getEndDate());

//        Map<LocalDate, Float> dateMap = new HashMap<>();
//        LocalDate currentDate = portfolionputDTO.getStartDate().withDayOfMonth(1);
//
//        while (!currentDate.isAfter(portfolionputDTO.getEndDate())) {
//            dateMap.put(currentDate, 1.0f);
//            currentDate = currentDate.plusMonths(1);
//        }

//        for (StockDTO stockDTO : stockDTOList) {
//            List<PortfolioStockPriceDTO> portfolioStockPriceDTOS = stockDTO.getPortfolioStockPriceList();
//            String name = stockDTO.getName();
//            for (PortfolioStockPriceDTO portfolioStockPriceDTO : portfolioStockPriceDTOS) {
//                portfolioStockPriceDTO.getClosePrice() -
//            }
//        }


//        List<String> stockNameList = portfolionputDTO.getPortfolioInputItemDTOList()
//                .stream()
//                .map(PortfolioInputItemDTO::getStockName)
//                .toList();
//
//        List<Float> weightList = portfolionputDTO.getPortfolioInputItemDTOList()
//                .stream()
//                .map(PortfolioInputItemDTO::getWeight)
//                .toList();
//
//        List<StockDTO> stockDTOList = stockService
//                .findStocksByNamesAndDateRange(stockNameList, portfolionputDTO.getStartDate(),
//                        portfolionputDTO.getEndDate());
//
//
//
//        Map<String, Map<LocalDate, Integer>> stockPriceMap = new HashMap<>();
//        for (StockDTO stockDTO : stockDTOList) {
//            Map<LocalDate, Integer> stockDateMap = stockDTO.getPortfolioStockPriceList().stream().collect(Collectors.toMap(ps->ps.getBaseDate(), ps->ps.getClosePrice()));
//            stockPriceMap.put(stockDTO.getName(), stockDateMap);
//        }
//
//        System.out.println(stockPriceMap);

//        return rorPortfolio;

        return 0.0f;
    }


}
