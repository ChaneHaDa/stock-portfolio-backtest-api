package com.chan.stock_portfolio_backtest_api.repository;

import com.chan.stock_portfolio_backtest_api.domain.CalcStockPrice;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CalcStockPriceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CalcStockPriceRepository calcStockPriceRepository;

    @Test
    void findByStockAndBaseDateBetween_ValidStockAndDateRange_ShouldReturnCorrectPrices() {
        // Given
        Stock testStock = Stock.builder()
                .name("테스트주식")
                .shortCode("123456")
                .build();
        testStock = entityManager.persistAndFlush(testStock);

        CalcStockPrice price1 = CalcStockPrice.builder()
                .stock(testStock)
                .baseDate(LocalDate.of(2023, 1, 1))
                .monthlyRor(5.0f)
                .build();
        entityManager.persistAndFlush(price1);

        CalcStockPrice price2 = CalcStockPrice.builder()
                .stock(testStock)
                .baseDate(LocalDate.of(2023, 2, 1))
                .monthlyRor(3.0f)
                .build();
        entityManager.persistAndFlush(price2);

        entityManager.clear();

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 2, 1);

        // When
        List<CalcStockPrice> result = calcStockPriceRepository
                .findByStockAndBaseDateBetween(testStock, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findByStockAndBaseDateBetween_NoDataInRange_ShouldReturnEmptyList() {
        // Given
        Stock testStock = Stock.builder()
                .name("테스트주식")
                .shortCode("123456")
                .build();
        testStock = entityManager.persistAndFlush(testStock);

        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 4, 1);

        // When
        List<CalcStockPrice> result = calcStockPriceRepository
                .findByStockAndBaseDateBetween(testStock, startDate, endDate);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByStockInAndBaseDateBetween_ValidStocksAndDateRange_ShouldReturnCorrectPrices() {
        // Given
        Stock stock1 = Stock.builder()
                .name("테스트주식1")
                .shortCode("123456")
                .build();
        stock1 = entityManager.persistAndFlush(stock1);

        Stock stock2 = Stock.builder()
                .name("테스트주식2")
                .shortCode("654321")
                .build();
        stock2 = entityManager.persistAndFlush(stock2);

        CalcStockPrice price1 = CalcStockPrice.builder()
                .stock(stock1)
                .baseDate(LocalDate.of(2023, 1, 1))
                .monthlyRor(5.0f)
                .build();
        entityManager.persistAndFlush(price1);

        CalcStockPrice price2 = CalcStockPrice.builder()
                .stock(stock2)
                .baseDate(LocalDate.of(2023, 1, 1))
                .monthlyRor(7.0f)
                .build();
        entityManager.persistAndFlush(price2);

        entityManager.clear();

        List<Stock> stocks = Arrays.asList(stock1, stock2);
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 2, 1);

        // When
        List<CalcStockPrice> result = calcStockPriceRepository
                .findByStockInAndBaseDateBetween(stocks, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findByStockInAndBaseDateBetween_EmptyStockList_ShouldReturnEmptyList() {
        // Given
        List<Stock> emptyStocks = Collections.emptyList();
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 2, 1);

        // When
        List<CalcStockPrice> result = calcStockPriceRepository
                .findByStockInAndBaseDateBetween(emptyStocks, startDate, endDate);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}