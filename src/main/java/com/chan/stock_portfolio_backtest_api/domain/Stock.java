package com.chan.stock_portfolio_backtest_api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
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
    @Column(unique = true)
    private String isinCode;
    private String marketCategory;
    private LocalDate startAt;
    private LocalDate endAt;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockPrice> stockPriceList = new ArrayList<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CalcStockPrice> calcStockPriceList = new ArrayList<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("startAt DESC")
    @Builder.Default
    private List<StockNameHistory> nameHistoryList = new ArrayList<>();
}
