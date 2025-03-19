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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;

    private Long amount;
    private LocalDate startDate;
    private LocalDate endDate;

    private Float ror;
    private Float volatility;

    private Float price;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PortfolioItem> portfolioItemList = new ArrayList<>();

    public void addPortfolioItem(PortfolioItem item) {
        portfolioItemList.add(item);
        item.setPortfolio(this);
    }

}
