package com.chan.stock_portfolio_backtest_api.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockPrice {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Integer closePrice;
	private Integer openPrice;
	private Integer lowPrice;
	private Integer highPrice;
	private Integer tradeQuantity;
	private Long tradeAmount;
	private Long issuedCount;
	private LocalDate baseDate;

	@ManyToOne
	@JoinColumn(name = "stock_id")
	@JsonBackReference
	private Stock stock;
}
