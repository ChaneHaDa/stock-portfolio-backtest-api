package com.chan.stock_portfolio_backtest_api.util;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

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

    /**
     * 연평균 수익률을 기반으로 지정된 기간의 월별 수익률을 생성합니다.
     * 
     * @param annualReturnRate 연평균 수익률 (백분율, 예: 10.5 = 10.5%)
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 월별 수익률을 담은 Map (키: 월 시작일, 값: 월별 수익률(%))
     */
    public static Map<LocalDate, Float> generateMonthlyRorFromAnnual(float annualReturnRate, 
                                                                    LocalDate startDate, 
                                                                    LocalDate endDate) {
        Map<LocalDate, Float> monthlyRorMap = new TreeMap<>();
        
        // 연평균 수익률을 월평균 수익률로 변환
        // 공식: monthlyRate = (1 + annualRate/100)^(1/12) - 1
        double annualRateDecimal = annualReturnRate / 100.0;
        double monthlyRateDecimal = Math.pow(1 + annualRateDecimal, 1.0/12.0) - 1;
        float monthlyRatePercent = (float) (monthlyRateDecimal * 100);
        
        // 시작월부터 종료월까지 모든 월에 동일한 수익률 적용
        LocalDate currentMonth = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);
        
        while (!currentMonth.isAfter(endMonth)) {
            monthlyRorMap.put(currentMonth, monthlyRatePercent);
            currentMonth = currentMonth.plusMonths(1);
        }
        
        return monthlyRorMap;
    }
}
