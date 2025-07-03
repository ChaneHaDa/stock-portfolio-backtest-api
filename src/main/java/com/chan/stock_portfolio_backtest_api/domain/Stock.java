package com.chan.stock_portfolio_backtest_api.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	@JsonManagedReference
	private List<StockPrice> stockPriceList = new ArrayList<>();

	@OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	@JsonManagedReference
	private List<CalcStockPrice> calcStockPriceList = new ArrayList<>();

	@OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("startAt DESC")
	@Builder.Default
	@JsonManagedReference
	private List<StockNameHistory> nameHistoryList = new ArrayList<>();
}
