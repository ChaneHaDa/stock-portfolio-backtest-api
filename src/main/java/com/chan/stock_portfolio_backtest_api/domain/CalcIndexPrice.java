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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalcIndexPrice {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Float price;
	private Float monthlyRor;
	private LocalDate baseDate;

	@ManyToOne
	@JoinColumn(name = "index_info_id")
	@JsonBackReference
	private IndexInfo indexInfo;

	public void setIndexInfo(IndexInfo indexInfo) {
		if (this.indexInfo != indexInfo) {
			this.indexInfo = indexInfo;
			if (indexInfo != null && !indexInfo.getCalcIndexPriceList().contains(this)) {
				indexInfo.getCalcIndexPriceList().add(this);
			}
		}
	}
}