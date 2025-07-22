package com.chan.stock_portfolio_backtest_api.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chan.stock_portfolio_backtest_api.dto.response.IndexPriceResponseDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.repository.IndexPriceRepository;

@Service
public class IndexPriceService {
	private final IndexPriceRepository indexPriceRepository;

	public IndexPriceService(IndexPriceRepository indexPriceRepository) {
		this.indexPriceRepository = indexPriceRepository;
	}

	public List<IndexPriceResponseDTO> findIndexPriceByIndexId(Integer stockId) {
		List<IndexPriceResponseDTO> indexPriceResponseDTOList = indexPriceRepository.findAllByIndexInfo_Id(stockId)
			.stream()
			.map(IndexPriceResponseDTO::entityToDTO)
			.toList();

		if (indexPriceResponseDTOList.isEmpty()) {
			throw new EntityNotFoundException("index price not found");
		}

		return indexPriceResponseDTOList;
	}

	public List<IndexPriceResponseDTO> findIndexPriceByIndexInfoIdAndDateRange(Integer id, LocalDate startDate,
		LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date must not be after end date.");
		}

		List<IndexPriceResponseDTO> indexPriceResponseDTOList = indexPriceRepository.findByIndexInfoIdAndDateRange(id,
				startDate, endDate)
			.stream()
			.map(IndexPriceResponseDTO::entityToDTO)
			.toList();

		if (indexPriceResponseDTOList.isEmpty()) {
			throw new EntityNotFoundException("index price not found");
		}

		return indexPriceResponseDTOList;
	}

}
