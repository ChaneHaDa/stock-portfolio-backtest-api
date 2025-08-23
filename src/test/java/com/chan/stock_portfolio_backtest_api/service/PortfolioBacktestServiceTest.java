package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.domain.CalcStockPrice;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestItemDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioBacktestResponseDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.exception.InvalidDateRangeException;
import com.chan.stock_portfolio_backtest_api.repository.CalcStockPriceRepository;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;
import com.chan.stock_portfolio_backtest_api.strategy.DataInterpolationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioBacktestServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private CalcStockPriceRepository calcStockPriceRepository;

    @Mock
    private DataInterpolationStrategy interpolationStrategy;

    @InjectMocks
    private PortfolioBacktestService portfolioBacktestService;

    private PortfolioBacktestRequestDTO requestDTO;
    private Stock testStock1;
    private Stock testStock2;
    private CalcStockPrice calcPrice1;
    private CalcStockPrice calcPrice2;

    @BeforeEach
    void setUp() {
        // Test stocks 설정
        testStock1 = Stock.builder()
                .id(1)
                .name("삼성전자")
                .shortCode("005930")
                .build();

        testStock2 = Stock.builder()
                .id(2)
                .name("SK하이닉스")
                .shortCode("000660")
                .build();

        // Test calc prices 설정
        LocalDate testDate1 = LocalDate.of(2023, 1, 1);
        LocalDate testDate2 = LocalDate.of(2023, 2, 1);

        calcPrice1 = CalcStockPrice.builder()
                .stock(testStock1)
                .baseDate(testDate1)
                .monthlyRor(5.0f)
                .build();

        calcPrice2 = CalcStockPrice.builder()
                .stock(testStock1)
                .baseDate(testDate2)
                .monthlyRor(3.0f)
                .build();

        // Test request DTO 설정
        PortfolioBacktestRequestItemDTO item1 = PortfolioBacktestRequestItemDTO.builder()
                .stockId(1)
                .weight(0.6f)
                .build();

        PortfolioBacktestRequestItemDTO item2 = PortfolioBacktestRequestItemDTO.builder()
                .stockId(2)
                .weight(0.4f)
                .build();

        requestDTO = PortfolioBacktestRequestDTO.builder()
                .amount(1000000L)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 2, 1))
                .portfolioBacktestRequestItemDTOList(Arrays.asList(item1, item2))
                .build();
    }

    @Test
    void calculatePortfolio_ValidRequest_ShouldReturnSuccessfulResult() {
        // Given
        when(stockRepository.findAllById(anyList()))
                .thenReturn(Arrays.asList(testStock1, testStock2));

        when(calcStockPriceRepository.findByStockInAndBaseDateBetween(anyList(), any(), any()))
                .thenReturn(Arrays.asList(calcPrice1, calcPrice2, calcPrice1, calcPrice2));

        Map<LocalDate, Float> interpolatedData = new TreeMap<>();
        interpolatedData.put(LocalDate.of(2023, 1, 1), 5.0f);
        interpolatedData.put(LocalDate.of(2023, 2, 1), 3.0f);

        when(interpolationStrategy.interpolate(any(), any(), any()))
                .thenReturn(interpolatedData);

        // When
        PortfolioBacktestResponseDTO result = portfolioBacktestService.calculatePortfolio(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(requestDTO, result.getPortfolioInput());
        assertNotNull(result.getPortfolioBacktestResponseItemDTOList());
        assertEquals(2, result.getPortfolioBacktestResponseItemDTOList().size());
        assertTrue(result.getTotalAmount() > 0);
        assertNotNull(result.getMonthlyRor());
        assertNotNull(result.getMonthlyAmount());

        // Verify interactions
        verify(stockRepository).findAllById(Arrays.asList(1, 2));
        verify(calcStockPriceRepository).findByStockInAndBaseDateBetween(anyList(), any(), any());
        verify(interpolationStrategy, times(2)).interpolate(any(), any(), any());
    }

    @Test
    void calculatePortfolio_StartDateAfterEndDate_ShouldThrowIllegalArgumentException() {
        // Given
        requestDTO = PortfolioBacktestRequestDTO.builder()
                .startDate(LocalDate.of(2023, 2, 1))
                .endDate(LocalDate.of(2023, 1, 1))
                .portfolioBacktestRequestItemDTOList(requestDTO.getPortfolioBacktestRequestItemDTOList())
                .amount(requestDTO.getAmount())
                .build();

        // When & Then
        InvalidDateRangeException exception = assertThrows(
                InvalidDateRangeException.class,
                () -> portfolioBacktestService.calculatePortfolio(requestDTO)
        );

        assertEquals("Start date must not be after end date.", exception.getMessage());

        // Verify no repository calls were made
        verify(stockRepository, never()).findAllById(any());
        verify(calcStockPriceRepository, never()).findByStockInAndBaseDateBetween(anyList(), any(), any());
    }

    @Test
    void calculatePortfolio_StocksNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        when(stockRepository.findAllById(anyList()))
                .thenReturn(Arrays.asList(testStock1)); // Only one stock returned, but two requested

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> portfolioBacktestService.calculatePortfolio(requestDTO)
        );

        assertEquals("Some stocks not found", exception.getMessage());

        // Verify repository call was made
        verify(stockRepository).findAllById(Arrays.asList(1, 2));
        verify(calcStockPriceRepository, never()).findByStockInAndBaseDateBetween(anyList(), any(), any());
    }

    @Test
    void calculatePortfolio_EmptyPortfolioItems_ShouldHandleGracefully() {
        // Given
        requestDTO = PortfolioBacktestRequestDTO.builder()
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .portfolioBacktestRequestItemDTOList(Collections.emptyList())
                .amount(requestDTO.getAmount())
                .build();

        when(stockRepository.findAllById(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        // When
        PortfolioBacktestResponseDTO result = portfolioBacktestService.calculatePortfolio(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(requestDTO, result.getPortfolioInput());
        assertNotNull(result.getPortfolioBacktestResponseItemDTOList());
        assertEquals(0, result.getPortfolioBacktestResponseItemDTOList().size());

        // Verify interactions
        verify(stockRepository).findAllById(Collections.emptyList());
        verify(calcStockPriceRepository, never()).findByStockInAndBaseDateBetween(anyList(), any(), any());
    }

    @Test
    void calculatePortfolio_SingleStock_ShouldCalculateCorrectly() {
        // Given
        PortfolioBacktestRequestItemDTO singleItem = PortfolioBacktestRequestItemDTO.builder()
                .stockId(1)
                .weight(1.0f)
                .build();

        requestDTO = PortfolioBacktestRequestDTO.builder()
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .portfolioBacktestRequestItemDTOList(Arrays.asList(singleItem))
                .amount(requestDTO.getAmount())
                .build();

        when(stockRepository.findAllById(Arrays.asList(1)))
                .thenReturn(Arrays.asList(testStock1));

        when(calcStockPriceRepository.findByStockInAndBaseDateBetween(anyList(), any(), any()))
                .thenReturn(Arrays.asList(calcPrice1, calcPrice2));

        Map<LocalDate, Float> interpolatedData = new TreeMap<>();
        interpolatedData.put(LocalDate.of(2023, 1, 1), 5.0f);
        interpolatedData.put(LocalDate.of(2023, 2, 1), 3.0f);

        when(interpolationStrategy.interpolate(any(), any(), any()))
                .thenReturn(interpolatedData);

        // When
        PortfolioBacktestResponseDTO result = portfolioBacktestService.calculatePortfolio(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPortfolioBacktestResponseItemDTOList().size());
        assertEquals("삼성전자", result.getPortfolioBacktestResponseItemDTOList().get(0).getName());

        // Verify interactions
        verify(stockRepository).findAllById(Arrays.asList(1));
        verify(calcStockPriceRepository).findByStockInAndBaseDateBetween(anyList(), 
                eq(LocalDate.of(2023, 1, 1)), eq(LocalDate.of(2023, 2, 1)));
        verify(interpolationStrategy).interpolate(any(), 
                eq(LocalDate.of(2023, 1, 1)), eq(LocalDate.of(2023, 2, 1)));
    }
}