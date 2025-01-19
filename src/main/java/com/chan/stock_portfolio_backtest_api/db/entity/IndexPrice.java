package com.chan.stock_portfolio_backtest_api.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Float closePrice;
    private Float openPrice;
    private Float lowPrice;
    private Float highPrice;
    private Float yearlyDiff;
    private LocalDate baseDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "index_info_id")
    private IndexInfo indexInfo;

}
