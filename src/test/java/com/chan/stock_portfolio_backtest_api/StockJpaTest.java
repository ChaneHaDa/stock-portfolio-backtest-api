package com.chan.stock_portfolio_backtest_api;

import com.chan.stock_portfolio_backtest_api.repository.StockPriceRepository;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StockJpaTest {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockPriceRepository stockPriceRepository;


//    @Test
//    void saveStockTest(){
//        Stock tempStock1 = new Stock(0, "삼성전자", "005930", "isincode", "KOSPI", null);
//        Stock savedStock1 = stockRepository.save(tempStock1);
//
//        LocalDate baseDate1 = LocalDate.of( 2024, 10, 25);
//        StockPrice tempStockPrice1 = new StockPrice(0, 1, 2, 3, 4, 5,6, 7, baseDate1, savedStock1);
//        StockPrice savedStockPrice1 = stockPriceRepository.save(tempStockPrice1);
//    }
//
//    @Test
//    @Transactional
//    void findStockTest(){
//        Stock tempStock1 = stockRepository.findById(1).orElse(null);
//
//        for (StockPrice s : tempStock1.getStockPriceList()) {
//            System.out.println(s.getId() + ": find ID");
//        }
//    }
}
