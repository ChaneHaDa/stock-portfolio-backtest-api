package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.domain.CalcIndexPrice;
import com.chan.stock_portfolio_backtest_api.domain.IndexInfo;
import com.chan.stock_portfolio_backtest_api.dto.request.IndexBacktestRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.IndexBacktestResponseDTO;
import com.chan.stock_portfolio_backtest_api.repository.CalcIndexPriceRepository;
import com.chan.stock_portfolio_backtest_api.repository.IndexInfoRepository;
import com.chan.stock_portfolio_backtest_api.util.PortfolioCalculator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class IndexBacktestService {

    private final IndexInfoRepository indexInfoRepository;
    private final CalcIndexPriceRepository calcIndexPriceRepository;

    public IndexBacktestService(IndexInfoRepository indexInfoRepository, CalcIndexPriceRepository calcIndexPriceRepository) {
        this.indexInfoRepository = indexInfoRepository;
        this.calcIndexPriceRepository = calcIndexPriceRepository;
    }

    public IndexBacktestResponseDTO calculateIndexBacktest(IndexBacktestRequestDTO requestDTO) {
        LocalDate startDate = requestDTO.getStartDate();
        LocalDate endDate = requestDTO.getEndDate();

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date.");
        }

        // 1. 인덱스 정보 조회
        String indexName = requestDTO.getName();
        IndexInfo indexInfo = indexInfoRepository.findByName(indexName);

        // 2. 인덱스의 CalcIndexPrice 데이터를 조회하여, 월별 수익률 Map 구성 (TreeMap으로 날짜 정렬 보장)
        List<CalcIndexPrice> calcPrices = calcIndexPriceRepository.findByIndexInfoAndBaseDateBetween(indexInfo, startDate, endDate);
        Map<LocalDate, Float> indexMonthlyRor = new TreeMap<>();
        for (CalcIndexPrice cip : calcPrices) {
            indexMonthlyRor.put(cip.getBaseDate(), cip.getMonthlyRor());
        }

        LocalDate startMonth = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);

        // 3. 전체 인덱스 누적(복리) 수익률 계산 (백분율)
        float totalRor = PortfolioCalculator.calculateCompoundRor(indexMonthlyRor, startMonth, endMonth);

        // 4. 변동성 계산 (월별 수익률의 표준편차)
        float volatility = PortfolioCalculator.calculateVolatility(indexMonthlyRor);

        // 5. 결과 DTO 생성 및 반환
        return IndexBacktestResponseDTO.builder()
                .totalRor(totalRor)
                .volatility(volatility)
                .monthlyRor(indexMonthlyRor)
                .build();
    }

}
