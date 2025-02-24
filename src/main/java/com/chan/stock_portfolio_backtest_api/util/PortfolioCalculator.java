package com.chan.stock_portfolio_backtest_api.util;

import java.time.LocalDate;
import java.util.Map;

public class PortfolioCalculator {

    /**
     * 주어진 월별 수익률 데이터를 복리로 누적하여 전체 수익률(백분율)을 계산합니다.
     *
     * @param monthlyRorMap 각 월별 수익률 (key: 시작일(yyyy-MM-01), value: 월 수익률(%))
     * @param startMonth    계산 시작 월 (해당 월의 1일)
     * @param endMonth      계산 종료 월 (해당 월의 1일)
     * @return 누적 수익률 (백분율, 예: 46.28)
     */
    public static float calculateCompoundRor(Map<LocalDate, Float> monthlyRorMap, LocalDate startMonth, LocalDate endMonth) {
        double compoundFactor = 1.0;
        LocalDate current = startMonth;
        while (!current.isAfter(endMonth)) {
            float monthlyRor = monthlyRorMap.getOrDefault(current, 0f);
            compoundFactor *= (1 + monthlyRor / 100.0);
            current = current.plusMonths(1);
        }
        // 결과를 백분율로 변환
        return (float) ((compoundFactor - 1.0) * 100);
    }

    /**
     * 각 주식의 월별 수익률(가중치 적용)을 포트폴리오의 월별 수익률에 합산합니다.
     *
     * @param portfolioRor 포트폴리오 전체 월별 수익률을 저장할 Map (key: 월 시작일, value: 수익률(%))
     * @param stockRor     개별 주식의 월별 수익률 Map
     * @param weight       해당 주식의 포트폴리오 내 투자 비중
     * @param startMonth   계산 시작 월 (해당 월의 1일)
     * @param endMonth     계산 종료 월 (해당 월의 1일)
     */
    public static void mergeStockIntoPortfolioRor(Map<LocalDate, Float> portfolioRor,
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
