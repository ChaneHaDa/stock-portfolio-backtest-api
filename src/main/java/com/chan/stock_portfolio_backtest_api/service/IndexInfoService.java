package com.chan.stock_portfolio_backtest_api.service;

import org.springframework.stereotype.Service;

import com.chan.stock_portfolio_backtest_api.dto.response.IndexInfoResponseDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.repository.IndexInfoRepository;

@Service
public class IndexInfoService {
	private final IndexInfoRepository indexInfoRepository;

	public IndexInfoService(IndexInfoRepository indexInfoRepository) {
		this.indexInfoRepository = indexInfoRepository;
	}

	public IndexInfoResponseDTO findIndexInfoById(Integer id) {
		return IndexInfoResponseDTO.entityToDTO(indexInfoRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("해당 Entity가 없습니다.")));
	}
}
