package com.chan.stock_portfolio_backtest_api.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioCalculatorTest {

    @Test
    void testGenerateMonthlyRorFromAnnual() {
        // Given: 연 10% 수익률
        float annualReturnRate = 10.0f;
        LocalDate startDate = LocalDate.of(2023, 1, 15);
        LocalDate endDate = LocalDate.of(2023, 3, 20);
        
        // When: 월별 수익률 생성
        Map<LocalDate, Float> monthlyRor = PortfolioCalculator.generateMonthlyRorFromAnnual(
            annualReturnRate, startDate, endDate);
        
        // Then: 결과 검증
        assertEquals(3, monthlyRor.size()); // 1월, 2월, 3월
        
        // 월별 수익률이 모두 동일해야 함
        float expectedMonthlyRate = (float) ((Math.pow(1.1, 1.0/12.0) - 1) * 100);
        
        assertTrue(monthlyRor.containsKey(LocalDate.of(2023, 1, 1)));
        assertTrue(monthlyRor.containsKey(LocalDate.of(2023, 2, 1)));
        assertTrue(monthlyRor.containsKey(LocalDate.of(2023, 3, 1)));
        
        // 각 월의 수익률이 예상값과 일치하는지 확인 (소수점 오차 허용)
        for (Float rate : monthlyRor.values()) {
            assertEquals(expectedMonthlyRate, rate, 0.001f);
        }
    }
    
    @Test
    void testCompoundCalculationAccuracy() {
        // Given: 연 12% 수익률로 1년간 백테스트
        float annualReturnRate = 12.0f;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        
        // When: 월별 수익률 생성 후 복리 계산
        Map<LocalDate, Float> monthlyRor = PortfolioCalculator.generateMonthlyRorFromAnnual(
            annualReturnRate, startDate, endDate);
        
        LocalDate startMonth = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);
        
        float compoundRor = PortfolioCalculator.calculateCompoundRor(monthlyRor, startMonth, endMonth);
        
        // Then: 복리 계산 결과가 원래 연수익률과 유사해야 함 (소수점 오차 허용)
        assertEquals(annualReturnRate, compoundRor, 0.1f);
    }
}