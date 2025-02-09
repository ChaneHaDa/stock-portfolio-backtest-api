package com.chan.stock_portfolio_backtest_api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalcStockPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer price;
    private float monthlyRor;
    private LocalDate baseDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;
}

