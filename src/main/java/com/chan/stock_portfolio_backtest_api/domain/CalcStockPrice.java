package com.chan.stock_portfolio_backtest_api.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = {
	@Index(name = "idx_calcstock_basedate", columnList = "stock_id, base_date")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalcStockPrice {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Float price;
	private Float monthlyRor;
	private LocalDate baseDate;

	@ManyToOne
	@JoinColumn(name = "stock_id")
	@JsonBackReference
	private Stock stock;

	public void setStock(Stock stock) {
		if (this.stock != stock) {
			this.stock = stock;
			if (stock != null && !stock.getCalcStockPriceList().contains(this)) {
				stock.getCalcStockPriceList().add(this);
			}
		}
	}
}
