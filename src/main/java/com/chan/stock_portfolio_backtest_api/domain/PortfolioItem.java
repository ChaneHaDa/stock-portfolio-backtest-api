package com.chan.stock_portfolio_backtest_api.domain;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Float weight;

	@ManyToOne
	@JoinColumn(name = "stock_id")
	private Stock stock;

	@ManyToOne
	@JoinColumn(name = "portfolio_id")
	@JsonBackReference
	private Portfolio portfolio;

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
}
