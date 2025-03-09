package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.domain.CalcStockPrice;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestItemDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioBacktestResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioBacktestResponseItemDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.repository.CalcStockPriceRepository;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;
import com.chan.stock_portfolio_backtest_api.util.PortfolioCalculator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PortfolioBacktestService {

    private final StockRepository stockRepository;
    private final CalcStockPriceRepository calcStockPriceRepository;

    public PortfolioBacktestService(StockRepository stockRepository, CalcStockPriceRepository calcStockPriceRepository) {
        this.stockRepository = stockRepository;
        this.calcStockPriceRepository = calcStockPriceRepository;
    }

    public PortfolioBacktestResponseDTO calculatePortfolio(PortfolioBacktestRequestDTO request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date.");
        }

        List<PortfolioBacktestRequestItemDTO> requestItems = request.getPortfolioBacktestRequestItemDTOList();

        // 전체 포트폴리오 월별 수익률을 누적할 Map
        Map<LocalDate, Float> portfolioMonthlyRor = new HashMap<>();

        // 개별 주식 결과를 저장할 리스트
        List<PortfolioBacktestResponseItemDTO> responseItemDTOs = new ArrayList<>();

        LocalDate startMonth = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);

        for (PortfolioBacktestRequestItemDTO item : requestItems) {
            // 1. 주식 엔티티 조회
            Stock stock = stockRepository.findByName(item.getStockName());
            if (stock == null) {
                throw new EntityNotFoundException("Stock with name '" + item.getStockName() + "' does not exist.");
            }

            // 2. 해당 주식의 월별 수익률 계산
            Map<LocalDate, Float> stockMonthlyRor = calculateStockMonthlyRor(stock, startDate, endDate);

            // 3. 주식별 누적(복리) 수익률 계산
            float stockTotalRor = PortfolioCalculator.calculateCompoundRor(stockMonthlyRor, startMonth, endMonth);

            // 4. 개별 주식 결과 DTO 생성
            PortfolioBacktestResponseItemDTO responseItem = PortfolioBacktestResponseItemDTO.builder()
                    .name(stock.getName())
                    .totalRor(stockTotalRor)
                    .monthlyRor(stockMonthlyRor)
                    .build();
            responseItemDTOs.add(responseItem);

            // 5. 포트폴리오 전체 월별 수익률에 가중치 적용하여 합산
            PortfolioCalculator.mergeStockIntoPortfolioRor(portfolioMonthlyRor, stockMonthlyRor, item.getWeight(), startMonth, endMonth);
        }

        // 6. 포트폴리오 전체 누적(복리) 수익률 계산
        float totalRor = PortfolioCalculator.calculateCompoundRor(new TreeMap<>(portfolioMonthlyRor), startMonth, endMonth);

        // 7. 월별 누적 자산 금액 계산 (초기 자산: request.getAmount())
        Map<LocalDate, Long> monthlyAmount = calculateMonthlyAmounts(portfolioMonthlyRor, startMonth, endMonth, request.getAmount());

        // 8. 변동성 계산 (전체 포트폴리오 월별 수익률 기반)
        float volatility = PortfolioCalculator.calculateVolatility(portfolioMonthlyRor);

        return PortfolioBacktestResponseDTO.builder()
                .portfolioInput(request)
                .totalRor(totalRor)
                .totalAmount((long) (request.getAmount() * (totalRor / 100 + 1)))
                .monthlyRor(new TreeMap<>(portfolioMonthlyRor))
                .monthlyAmount(monthlyAmount)
                .volatility(volatility)
                .portfolioBacktestResponseItemDTOList(responseItemDTOs)
                .build();
    }

    /**
     * 주어진 주식(stock)의 CalcStockPrice 데이터를 조회하여,
     * 시작 날짜(startDate)와 종료 날짜(endDate) 사이에 해당하는 월별 수익률을 계산합니다.
     */
    private Map<LocalDate, Float> calculateStockMonthlyRor(Stock stock, LocalDate startDate, LocalDate endDate) {
        List<CalcStockPrice> calcPrices = calcStockPriceRepository.findByStockAndBaseDateBetween(stock, startDate, endDate);
        Map<LocalDate, Float> stockMonthlyRor = new TreeMap<>();
        for (CalcStockPrice csp : calcPrices) {
            stockMonthlyRor.put(csp.getBaseDate(), csp.getMonthlyRor());
        }
        return stockMonthlyRor;
    }

    /**
     * 포트폴리오의 월별 수익률을 기반으로 누적 자산 금액을 계산합니다.
     * 초기 자산에서 시작하여 매월 해당 월의 수익률을 적용한 후, 누적 금액을 계산합니다.
     */
    private Map<LocalDate, Long> calculateMonthlyAmounts(Map<LocalDate, Float> monthlyRorMap,
                                                         LocalDate startMonth,
                                                         LocalDate endMonth,
                                                         Long initialAmount) {
        Map<LocalDate, Long> monthlyAmounts = new TreeMap<>();
        double currentAmount = initialAmount; // double로 계산하여 정밀도 유지
        LocalDate current = startMonth;
        while (!current.isAfter(endMonth)) {
            // 월별 수익률을 가져와서 누적 계산 (월별 수익률은 백분율임)
            float monthlyRor = monthlyRorMap.getOrDefault(current, 0f);
            currentAmount = currentAmount * (1 + monthlyRor / 100.0);
            monthlyAmounts.put(current, (long) currentAmount);
            current = current.plusMonths(1);
        }
        return monthlyAmounts;
    }

}
