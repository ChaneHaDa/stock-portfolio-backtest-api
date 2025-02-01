package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.domain.CalcStockPrice;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestItemDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseItemDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.repository.CalcStockPriceRepository;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PortfolioService {
    private final StockRepository stockRepository;
    private final CalcStockPriceRepository calcStockPriceRepository;

    public PortfolioService(StockRepository stockRepository, CalcStockPriceRepository calcStockPriceRepository) {
        this.stockRepository = stockRepository;
        this.calcStockPriceRepository = calcStockPriceRepository;
    }

    public PortfolioResponseDTO calculatePortfolio(PortfolioRequestDTO request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        List<PortfolioRequestItemDTO> requestItems = request.getPortfolioRequestItemDTOList();

        Map<LocalDate, Float> portfolioMonthlyRor = new HashMap<>();

        // 개별 주식 결과를 저장할 리스트
        List<PortfolioResponseItemDTO> responseItemDTOs = new ArrayList<>();

        // 날짜 포맷 (CalcStockPrice의 baseDate 형식과 동일하게 "yyyy-MM-dd")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate startMonth = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);

        // 각 포트폴리오 항목(주식)에 대해 처리
        for (PortfolioRequestItemDTO item : requestItems) {
            String stockName = item.getStockName();
            float weight = item.getWeight();

            // 1. stockName으로 Stock 엔티티 조회
            Stock stock = stockRepository.findByName(stockName);
            if (stock == null) {
                throw new EntityNotFoundException("stockName은 존재하지 않습니다");
            }

            // 2. 해당 주식의 CalcStockPrice 데이터를 시작일 ~ 종료일 사이로 조회
            List<CalcStockPrice> calcPrices = calcStockPriceRepository.findByStockAndBaseDateBetween(stock, startDate, endDate);

            // 3. 해당 주식의 월별 수익률을 Map에 저장 (정렬을 위해 TreeMap 사용)
            Map<LocalDate, Float> stockMonthlyRor = new TreeMap<>();
            for (CalcStockPrice csp : calcPrices) {
                stockMonthlyRor.put(csp.getBaseDate(), csp.getMonthlyRor());
            }

            // 4. 주식별 누적(복리) 수익률 계산
            double compoundFactor = 1.0;
            LocalDate current = startMonth;
            while (!current.isAfter(endMonth)) {
                float monthlyRor = stockMonthlyRor.getOrDefault(current, 0f);
                compoundFactor *= (1 + monthlyRor / 100.0);
                current = current.plusMonths(1);
            }
            float stockTotalRor = (float) (compoundFactor - 1.0) * 100;

            // 5. 개별 주식 결과 DTO 생성
            PortfolioResponseItemDTO responseItem = new PortfolioResponseItemDTO();
            responseItem.setName(stock.getName());
            responseItem.setTotalRor(stockTotalRor);
            responseItem.setMonthlyRor(stockMonthlyRor);
            responseItemDTOs.add(responseItem);

            // 6. 포트폴리오 전체 월별 수익률에 가중치 적용하여 합산
            current = startMonth;
            while (!current.isAfter(endMonth)) {
                float monthlyRor = stockMonthlyRor.getOrDefault(current, 0f);
                // 각 월에 대해 가중치가 적용된 수익률을 합산 (동일 월에 여러 주식의 가중치 수익률 합산)
                portfolioMonthlyRor.merge(current, weight * monthlyRor, Float::sum);
                current = current.plusMonths(1);
            }
        }

        // 7. 전체 포트폴리오의 누적(복리) 수익률 계산 (포트폴리오 월별 수익률를 복리로 누적)
        double portfolioCompoundFactor = 1.0;
        // 정렬된 순서대로 처리하기 위해 TreeMap 사용
        Map<LocalDate, Float> sortedPortfolioMonthlyRor = new TreeMap<>(portfolioMonthlyRor);
        for (Map.Entry<LocalDate, Float> entry : sortedPortfolioMonthlyRor.entrySet()) {
            float monthlyRor = entry.getValue();
            portfolioCompoundFactor *= (1 + monthlyRor / 100.0);
        }
        float totalRor = (float) (portfolioCompoundFactor - 1.0) * 100;

        // 8. 결과 DTO 생성 및 반환
        PortfolioResponseDTO responseDTO = new PortfolioResponseDTO();
        responseDTO.setPortfolionput(request);
        responseDTO.setTotalRor(totalRor);
        responseDTO.setMonthlyRor(sortedPortfolioMonthlyRor);
        responseDTO.setPortfolioResponseItemDTOS(responseItemDTOs);

        return responseDTO;
    }
}
