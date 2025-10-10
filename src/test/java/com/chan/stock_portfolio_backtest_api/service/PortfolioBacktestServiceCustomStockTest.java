package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestItemDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioBacktestResponseDTO;
import com.chan.stock_portfolio_backtest_api.repository.CalcStockPriceRepository;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PortfolioBacktestServiceCustomStockTest {

    @Mock
    private StockRepository stockRepository;
    
    @Mock
    private CalcStockPriceRepository calcStockPriceRepository;
    
    @Mock
    private MetricsService metricsService;
    
    @InjectMocks
    private PortfolioBacktestService portfolioBacktestService;

    @Test
    void testCalculatePortfolioWithCustomStock() {
        // Given: 사용자 정의 종목만 포함된 포트폴리오
        PortfolioBacktestRequestItemDTO customStock1 = PortfolioBacktestRequestItemDTO.builder()
                .customStockName("사용자정의종목A")
                .annualReturnRate(10.0f)
                .weight(0.6f)
                .build();
                
        PortfolioBacktestRequestItemDTO customStock2 = PortfolioBacktestRequestItemDTO.builder()
                .customStockName("사용자정의종목B")
                .annualReturnRate(15.0f)
                .weight(0.4f)
                .build();
        
        PortfolioBacktestRequestDTO request = PortfolioBacktestRequestDTO.builder()
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .amount(1000000L)
                .portfolioBacktestRequestItemDTOList(Arrays.asList(customStock1, customStock2))
                .build();
        
        // When: 백테스트 수행
        PortfolioBacktestResponseDTO result = portfolioBacktestService.calculatePortfolio(request);
        
        // Then: 결과 검증
        assertNotNull(result);
        assertEquals(2, result.getPortfolioBacktestResponseItemDTOList().size());
        
        // 가중평균 수익률: 10% * 0.6 + 15% * 0.4 = 12%
        float expectedPortfolioReturn = 12.0f;
        assertEquals(expectedPortfolioReturn, result.getTotalRor(), 0.1f);
        
        // 개별 종목 결과 확인
        assertEquals("사용자정의종목A", result.getPortfolioBacktestResponseItemDTOList().get(0).getName());
        assertEquals("사용자정의종목B", result.getPortfolioBacktestResponseItemDTOList().get(1).getName());
        
        // 종목별 수익률 확인
        assertEquals(10.0f, result.getPortfolioBacktestResponseItemDTOList().get(0).getTotalRor(), 0.1f);
        assertEquals(15.0f, result.getPortfolioBacktestResponseItemDTOList().get(1).getTotalRor(), 0.1f);
    }
    
    @Test
    void testCustomStockValidation() {
        // Given: 잘못된 사용자 정의 종목 (이름만 있고 수익률 없음)
        PortfolioBacktestRequestItemDTO invalidCustomStock = PortfolioBacktestRequestItemDTO.builder()
                .customStockName("잘못된종목")
                .weight(1.0f)
                .build();
        
        PortfolioBacktestRequestDTO request = PortfolioBacktestRequestDTO.builder()
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .amount(1000000L)
                .portfolioBacktestRequestItemDTOList(Arrays.asList(invalidCustomStock))
                .build();
        
        // When & Then: 예외 발생해야 함
        assertThrows(Exception.class, () -> {
            portfolioBacktestService.calculatePortfolio(request);
        });
    }
}
