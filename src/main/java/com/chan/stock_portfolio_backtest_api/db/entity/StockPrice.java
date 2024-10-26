package com.chan.stock_portfolio_backtest_api.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer closePrice;
    private Integer openPrice;
    private Integer lowPrice;
    private Integer highPrice;
    private Integer tradeQuantity;
    private Integer tradeAmount;
    private Integer issuedCount;
    private LocalDate baseDate;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;
}
