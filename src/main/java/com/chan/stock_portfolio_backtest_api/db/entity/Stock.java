package com.chan.stock_portfolio_backtest_api.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String shortCode;
    private String isinCode;
    private String marketCategory;

    @OneToMany(mappedBy = "stock")
    private List<StockPrice> stockPriceList;

    @OneToMany(mappedBy = "stock")
    private List<PortfolioStockPrice> portfolioStockPriceList;
}
