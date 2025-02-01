package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.domain.CalcStockPrice;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestItemDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseItemDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.repository.CalcStockPriceRepository;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PortfolioService {

    private final StockRepository stockRepository;
    private final CalcStockPriceRepository calcStockPriceRepository;

    public PortfolioService(StockRepository stockRepository,
                            CalcStockPriceRepository calcStockPriceRepository) {
        this.stockRepository = stockRepository;
        this.calcStockPriceRepository = calcStockPriceRepository;
    }

    public PortfolioResponseDTO calculatePortfolio(PortfolioRequestDTO request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date.");
        }

        List<PortfolioRequestItemDTO> requestItems = request.getPortfolioRequestItemDTOList();

        // 전체 포트폴리오 월별 수익률을 누적할 Map
        Map<LocalDate, Float> portfolioMonthlyRor = new HashMap<>();
        // 개별 주식 결과를 저장할 리스트
        List<PortfolioResponseItemDTO> responseItemDTOs = new ArrayList<>();

        LocalDate startMonth = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);

        // 각 포트폴리오 항목(주식)에 대해 처리
        for (PortfolioRequestItemDTO item : requestItems) {
            // 1. 주식 엔티티 조회
            Stock stock = stockRepository.findByName(item.getStockName());
            if (stock == null) {
                throw new EntityNotFoundException("Stock with name '" + item.getStockName() + "' does not exist.");
            }

            // 2. 해당 주식의 월별 수익률 계산
            Map<LocalDate, Float> stockMonthlyRor = calculateStockMonthlyRor(stock, startDate, endDate);

            // 3. 주식별 누적(복리) 수익률 계산
            float stockTotalRor = calculateCompoundRor(stockMonthlyRor, startMonth, endMonth);

            // 4. 개별 주식 결과 DTO 생성
            PortfolioResponseItemDTO responseItem = PortfolioResponseItemDTO.builder()
                    .name(stock.getName())
                    .totalRor(stockTotalRor)
                    .monthlyRor(stockMonthlyRor)
                    .build();
            responseItemDTOs.add(responseItem);

            // 5. 포트폴리오 전체 월별 수익률에 가중치 적용하여 합산
            mergeStockIntoPortfolioRor(portfolioMonthlyRor, stockMonthlyRor, item.getWeight(), startMonth, endMonth);
        }

        // 6. 포트폴리오 전체 누적(복리) 수익률 계산
        float totalRor = calculateCompoundRor(new TreeMap<>(portfolioMonthlyRor), startMonth, endMonth);

        return PortfolioResponseDTO.builder()
                .portfolioInput(request)
                .totalRor(totalRor)
                .monthlyRor(new TreeMap<>(portfolioMonthlyRor))
                .portfolioResponseItemDTOS(responseItemDTOs)
                .build();
    }

    // 월별 수익률 계산
    private Map<LocalDate, Float> calculateStockMonthlyRor(Stock stock, LocalDate startDate, LocalDate endDate) {
        List<CalcStockPrice> calcPrices = calcStockPriceRepository.findByStockAndBaseDateBetween(stock, startDate, endDate);
        Map<LocalDate, Float> stockMonthlyRor = new TreeMap<>();
        for (CalcStockPrice csp : calcPrices) {
            stockMonthlyRor.put(csp.getBaseDate(), csp.getMonthlyRor());
        }
        return stockMonthlyRor;
    }

    // 월별 수익률을 기반으로 누적 수익률 계산
    private float calculateCompoundRor(Map<LocalDate, Float> monthlyRorMap, LocalDate startMonth, LocalDate endMonth) {
        double compoundFactor = 1.0;
        LocalDate current = startMonth;
        while (!current.isAfter(endMonth)) {
            float monthlyRor = monthlyRorMap.getOrDefault(current, 0f);
            compoundFactor *= (1 + monthlyRor / 100.0);
            current = current.plusMonths(1);
        }
        return (float) ((compoundFactor - 1.0) * 100);
    }

    // 포트폴리오 수익률 계산
    private void mergeStockIntoPortfolioRor(Map<LocalDate, Float> portfolioRor,
                                            Map<LocalDate, Float> stockRor,
                                            float weight,
                                            LocalDate startMonth,
                                            LocalDate endMonth) {
        LocalDate current = startMonth;
        while (!current.isAfter(endMonth)) {
            float monthlyRor = stockRor.getOrDefault(current, 0f);
            portfolioRor.merge(current, weight * monthlyRor, Float::sum);
            current = current.plusMonths(1);
        }
    }
}
