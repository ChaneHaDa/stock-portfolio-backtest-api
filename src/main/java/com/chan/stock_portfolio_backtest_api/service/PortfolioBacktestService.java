package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.domain.CalcStockPrice;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestItemDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioBacktestResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioBacktestResponseItemDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.exception.InvalidDateRangeException;
import com.chan.stock_portfolio_backtest_api.constants.AppConstants;
import com.chan.stock_portfolio_backtest_api.repository.CalcStockPriceRepository;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;
import com.chan.stock_portfolio_backtest_api.strategy.DataInterpolationStrategy;
import com.chan.stock_portfolio_backtest_api.util.PortfolioCalculator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * 포트폴리오 백테스트 서비스
 * 주어진 기간 동안의 포트폴리오 성과를 계산하고 분석하는 서비스입니다.
 */
@Service
public class PortfolioBacktestService {


    private final StockRepository stockRepository;
    private final CalcStockPriceRepository calcStockPriceRepository;
    private final DataInterpolationStrategy interpolationStrategy;

    public PortfolioBacktestService(
            StockRepository stockRepository,
            CalcStockPriceRepository calcStockPriceRepository,
            DataInterpolationStrategy interpolationStrategy) {
        this.stockRepository = stockRepository;
        this.calcStockPriceRepository = calcStockPriceRepository;
        this.interpolationStrategy = interpolationStrategy;
    }

    /**
     * 포트폴리오 백테스트를 수행하여 성과를 계산합니다.
     *
     * @param request 포트폴리오 백테스트 요청 DTO
     * @return 포트폴리오 백테스트 결과 DTO
     * @throws InvalidDateRangeException 시작일이 종료일보다 늦은 경우
     * @throws EntityNotFoundException  요청된 주식이 존재하지 않는 경우
     */
    public PortfolioBacktestResponseDTO calculatePortfolio(PortfolioBacktestRequestDTO request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException(AppConstants.DATE_VALIDATION_ERROR);
        }

        List<PortfolioBacktestRequestItemDTO> requestItems = request.getPortfolioBacktestRequestItemDTOList();

        // 전체 포트폴리오 월별 수익률을 누적할 Map
        Map<LocalDate, Float> portfolioMonthlyRor = new HashMap<>();

        // 개별 주식 결과를 저장할 리스트
        List<PortfolioBacktestResponseItemDTO> responseItemDTOs = new ArrayList<>();

        LocalDate startMonth = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);

        // 1. 모든 주식 ID를 한 번에 조회하여 N+1 쿼리 문제 해결
        List<Integer> stockIds = requestItems.stream()
                .map(PortfolioBacktestRequestItemDTO::getStockId)
                .toList();
        
        List<Stock> stocks = stockRepository.findAllById(stockIds);
        
        if (stocks.size() != stockIds.size()) {
            throw new EntityNotFoundException("Some stocks not found");
        }
        
        Map<Integer, Stock> stockMap = stocks.stream()
                .collect(java.util.stream.Collectors.toMap(Stock::getId, stock -> stock));

        for (PortfolioBacktestRequestItemDTO item : requestItems) {
            // 주식 엔티티를 Map에서 조회
            Stock stock = stockMap.get(item.getStockId());

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
                .totalAmount((long) (request.getAmount() * (totalRor / AppConstants.PERCENTAGE_CONVERSION_FACTOR + 1)))
                .monthlyRor(new TreeMap<>(portfolioMonthlyRor))
                .monthlyAmount(monthlyAmount)
                .volatility(volatility)
                .portfolioBacktestResponseItemDTOList(responseItemDTOs)
                .build();
    }

    /**
     * 주어진 주식의 월별 수익률을 계산합니다.
     *
     * @param stock     계산할 주식 엔티티
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 월별 수익률을 담은 Map (날짜: 수익률)
     */
    private Map<LocalDate, Float> calculateStockMonthlyRor(Stock stock, LocalDate startDate, LocalDate endDate) {
        List<CalcStockPrice> calcPrices = calcStockPriceRepository.findByStockAndBaseDateBetween(stock, startDate, endDate);
        Map<LocalDate, Float> stockMonthlyRor = new TreeMap<>();

        // 실제 데이터 포인트 저장
        for (CalcStockPrice csp : calcPrices) {
            stockMonthlyRor.put(csp.getBaseDate(), csp.getMonthlyRor());
        }

        // 데이터가 없는 날짜에 대한 보간 처리
        return interpolationStrategy.interpolate(stockMonthlyRor, startDate, endDate);
    }

    /**
     * 포트폴리오의 월별 누적 자산 금액을 계산합니다.
     *
     * @param monthlyRorMap 월별 수익률 Map
     * @param startMonth    시작 월
     * @param endMonth      종료 월
     * @param initialAmount 초기 투자 금액
     * @return 월별 누적 자산 금액을 담은 Map (날짜: 금액)
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
            float monthlyRor = monthlyRorMap.getOrDefault(current, AppConstants.DEFAULT_MONTHLY_ROR);
            currentAmount = currentAmount * (1 + monthlyRor / AppConstants.PERCENTAGE_CONVERSION_FACTOR);
            monthlyAmounts.put(current, (long) currentAmount);
            current = current.plusMonths(1);
        }
        return monthlyAmounts;
    }
}
