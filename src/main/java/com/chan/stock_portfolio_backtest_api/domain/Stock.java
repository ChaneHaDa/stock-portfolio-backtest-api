package com.chan.stock_portfolio_backtest_api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    private List<CalcStockPrice> calcStockPriceList;
}
