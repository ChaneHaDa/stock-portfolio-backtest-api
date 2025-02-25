package com.chan.stock_portfolio_backtest_api.util;

import java.time.LocalDate;
import java.util.Collection;
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


    /**
     * 주어진 월별 수익률 데이터를 이용해 표준편차(변동성)를 계산합니다.
     * 월별 수익률은 백분율로 저장되어 있다고 가정합니다.
     *
     * @param monthlyRorMap 월별 수익률을 담은 Map (키: LocalDate, 값: 월별 수익률(%))
     * @return 월별 수익률의 표준편차 (변동성, 백분율 단위)
     */
    public static float calculateVolatility(Map<LocalDate, Float> monthlyRorMap) {
        Collection<Float> returns = monthlyRorMap.values();
        int n = returns.size();
        if (n == 0) {
            return 0f;
        }

        // 평균 수익률 계산
        double sum = returns.stream().mapToDouble(r -> r).sum();
        double mean = sum / n;

        // 분산 계산
        double variance = returns.stream()
                .mapToDouble(r -> Math.pow(r - mean, 2))
                .sum() / n;

        double stdDev = Math.sqrt(variance);

        // 결과는 월별 변동성이며, 필요한 경우 연율화: stdDev * Math.sqrt(12)
        return (float) stdDev;
    }
}
