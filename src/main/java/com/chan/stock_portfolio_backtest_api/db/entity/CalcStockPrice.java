package com.chan.stock_portfolio_backtest_api.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalcStockPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer closePrice;
    private LocalDate baseDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;
}

