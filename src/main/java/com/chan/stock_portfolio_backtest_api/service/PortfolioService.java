package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.db.dto.StockDTO;
import com.chan.stock_portfolio_backtest_api.db.dto.StockPriceDTO;
import com.chan.stock_portfolio_backtest_api.db.service.StockService;
import com.chan.stock_portfolio_backtest_api.dto.PortfolioInputItemDTO;
import com.chan.stock_portfolio_backtest_api.dto.PortfolionputDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        Map<String, List<Integer>> prices = new HashMap<>();

        for (StockDTO i : stockDTOList) {
            List<Integer> t = i.getStockPriceList().stream().map(StockPriceDTO::getClosePrice).toList();
            prices.put(i.getName(), t);
        }

        for (int i = 0; i < weightList.size(); i++) {
            int start_price = prices.get(stockNameList.get(i)).get(0);
            int end_price = prices.get(stockNameList.get(i)).get(prices.get(stockNameList.get(i)).size() - 1);
            rorPortfolio += (float) (end_price - start_price) / start_price * weightList.get(i);
        }

        return rorPortfolio;
    }

}
