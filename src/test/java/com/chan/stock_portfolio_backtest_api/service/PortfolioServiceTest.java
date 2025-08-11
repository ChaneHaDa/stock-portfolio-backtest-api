package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.domain.Portfolio;
import com.chan.stock_portfolio_backtest_api.domain.PortfolioItem;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import com.chan.stock_portfolio_backtest_api.domain.Users;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioItemRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioDetailResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.repository.PortfolioRepository;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;
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
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private AuthService authService;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private Users testUser;
    private Stock testStock1;
    private Stock testStock2;
    private Portfolio testPortfolio;
    private PortfolioRequestDTO portfolioRequestDTO;

    @BeforeEach
    void setUp() {
        // Test user 설정
        testUser = Users.builder()
                .id(1)
                .username("testuser")
                .email("test@test.com")
                .build();

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

        // Test portfolio 설정
        testPortfolio = Portfolio.builder()
                .id(1)
                .name("테스트 포트폴리오")
                .description("테스트용 포트폴리오")
                .amount(1000000L)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .user(testUser)
                .build();

        // Test portfolio items 설정
        PortfolioItem item1 = PortfolioItem.builder()
                .stock(testStock1)
                .weight(0.6f)
                .build();
        
        PortfolioItem item2 = PortfolioItem.builder()
                .stock(testStock2)
                .weight(0.4f)
                .build();

        // 포트폴리오 아이템 추가
        testPortfolio.addPortfolioItem(item1);
        testPortfolio.addPortfolioItem(item2);

        // Test request DTO 설정
        PortfolioItemRequestDTO itemRequestDTO1 = PortfolioItemRequestDTO.builder()
                .stockId(1)
                .weight(0.6f)
                .build();

        PortfolioItemRequestDTO itemRequestDTO2 = PortfolioItemRequestDTO.builder()
                .stockId(2)
                .weight(0.4f)
                .build();

        portfolioRequestDTO = PortfolioRequestDTO.builder()
                .name("테스트 포트폴리오")
                .description("테스트용 포트폴리오")
                .amount(1000000L)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .portfolioItemRequestDTOList(Arrays.asList(itemRequestDTO1, itemRequestDTO2))
                .build();
    }

    @Test
    void createPortfolio_ValidRequest_ShouldCreateSuccessfully() {
        // Given
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(stockRepository.findAllById(anyList()))
                .thenReturn(Arrays.asList(testStock1, testStock2));
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(testPortfolio);

        // When
        PortfolioResponseDTO result = portfolioService.createPortfolio(portfolioRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals("테스트 포트폴리오", result.getName());
        assertEquals("테스트용 포트폴리오", result.getDescription());
        assertEquals(1000000L, result.getAmount());

        // Verify interactions
        verify(authService).getCurrentUser();
        verify(stockRepository).findAllById(Arrays.asList(1, 2));
        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    void createPortfolio_StocksNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(stockRepository.findAllById(anyList()))
                .thenReturn(Arrays.asList(testStock1)); // Only one stock returned

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> portfolioService.createPortfolio(portfolioRequestDTO)
        );

        assertEquals("Some stocks not found", exception.getMessage());

        // Verify interactions
        verify(authService).getCurrentUser();
        verify(stockRepository).findAllById(Arrays.asList(1, 2));
        verify(portfolioRepository, never()).save(any());
    }

    @Test
    void findPortfolioByUser_ExistingPortfolios_ShouldReturnPortfolioList() {
        // Given
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(portfolioRepository.findAllByUser(testUser))
                .thenReturn(Arrays.asList(testPortfolio));

        // When
        List<PortfolioResponseDTO> result = portfolioService.findPortfolioByUser();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("테스트 포트폴리오", result.get(0).getName());

        // Verify interactions
        verify(authService).getCurrentUser();
        verify(portfolioRepository).findAllByUser(testUser);
    }

    @Test
    void findPortfolioByUser_NoPortfolios_ShouldThrowEntityNotFoundException() {
        // Given
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(portfolioRepository.findAllByUser(testUser))
                .thenReturn(Collections.emptyList());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> portfolioService.findPortfolioByUser()
        );

        assertEquals("Portfolio Not Found", exception.getMessage());

        // Verify interactions
        verify(authService).getCurrentUser();
        verify(portfolioRepository).findAllByUser(testUser);
    }

    @Test
    void findPortfolioById_ValidIdAndOwner_ShouldReturnPortfolioDetail() {
        // Given
        when(portfolioRepository.findById(1)).thenReturn(Optional.of(testPortfolio));
        when(authService.getCurrentUser()).thenReturn(testUser);

        // When
        PortfolioDetailResponseDTO result = portfolioService.findPortfolioById(1);

        // Then
        assertNotNull(result);
        assertEquals("테스트 포트폴리오", result.getName());
        assertEquals("테스트용 포트폴리오", result.getDescription());

        // Verify interactions
        verify(portfolioRepository).findById(1);
        verify(authService).getCurrentUser();
    }

    @Test
    void findPortfolioById_InvalidOwner_ShouldThrowEntityNotFoundException() {
        // Given
        Users otherUser = Users.builder()
                .id(2)
                .username("otheruser")
                .email("other@test.com")
                .build();

        when(portfolioRepository.findById(1)).thenReturn(Optional.of(testPortfolio));
        when(authService.getCurrentUser()).thenReturn(otherUser);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> portfolioService.findPortfolioById(1)
        );

        assertEquals("Portfolio not found", exception.getMessage());

        // Verify interactions
        verify(portfolioRepository).findById(1);
        verify(authService).getCurrentUser();
    }

    @Test
    void findPortfolioById_NotFound_ShouldThrowEntityNotFoundException() {
        // Given
        when(portfolioRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> portfolioService.findPortfolioById(999)
        );

        // Verify interactions
        verify(portfolioRepository).findById(999);
        verify(authService, never()).getCurrentUser();
    }

    @Test
    void deletePortfolio_ValidIdAndOwner_ShouldDeleteSuccessfully() {
        // Given
        when(portfolioRepository.findById(1)).thenReturn(Optional.of(testPortfolio));
        when(authService.getCurrentUser()).thenReturn(testUser);

        // When
        portfolioService.deletePortfolio(1);

        // Then
        verify(portfolioRepository).findById(1);
        verify(authService).getCurrentUser();
        verify(portfolioRepository).delete(testPortfolio);
    }

    @Test
    void deletePortfolio_InvalidOwner_ShouldThrowEntityNotFoundException() {
        // Given
        Users otherUser = Users.builder()
                .id(2)
                .username("otheruser")
                .email("other@test.com")
                .build();

        when(portfolioRepository.findById(1)).thenReturn(Optional.of(testPortfolio));
        when(authService.getCurrentUser()).thenReturn(otherUser);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> portfolioService.deletePortfolio(1)
        );

        assertEquals("Portfolio Not Found", exception.getMessage());

        // Verify interactions
        verify(portfolioRepository).findById(1);
        verify(authService).getCurrentUser();
        verify(portfolioRepository, never()).delete(any());
    }

    @Test
    void updatePortfolio_ValidRequest_ShouldUpdateSuccessfully() {
        // Given
        when(portfolioRepository.findById(1)).thenReturn(Optional.of(testPortfolio));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(stockRepository.findAllById(anyList()))
                .thenReturn(Arrays.asList(testStock1, testStock2));

        // When
        PortfolioResponseDTO result = portfolioService.updatePortfolio(1, portfolioRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals("테스트 포트폴리오", result.getName());

        // Verify interactions
        verify(portfolioRepository).findById(1);
        verify(authService).getCurrentUser();
        verify(stockRepository).findAllById(Arrays.asList(1, 2));
    }

    @Test
    void updatePortfolio_InvalidOwner_ShouldThrowEntityNotFoundException() {
        // Given
        Users otherUser = Users.builder()
                .id(2)
                .username("otheruser")
                .email("other@test.com")
                .build();

        when(portfolioRepository.findById(1)).thenReturn(Optional.of(testPortfolio));
        when(authService.getCurrentUser()).thenReturn(otherUser);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> portfolioService.updatePortfolio(1, portfolioRequestDTO)
        );

        assertEquals("Portfolio not found", exception.getMessage());

        // Verify interactions
        verify(portfolioRepository).findById(1);
        verify(authService).getCurrentUser();
        verify(stockRepository, never()).findAllById(any());
    }
}