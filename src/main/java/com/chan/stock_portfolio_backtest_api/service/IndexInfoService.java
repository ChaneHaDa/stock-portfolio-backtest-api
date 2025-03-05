package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.dto.response.IndexInfoResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.IndexPriceResponseDTO;
import com.chan.stock_portfolio_backtest_api.repository.IndexInfoRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class IndexInfoService {
    private final IndexInfoRepository indexInfoRepository;

    public IndexInfoService(IndexInfoRepository indexInfoRepository) {
        this.indexInfoRepository = indexInfoRepository;
    }

    public IndexInfoResponseDTO findIndexInfoById(Integer id) {
        return IndexInfoResponseDTO.entityToDTO(indexInfoRepository.findById(id).orElse(null));
    }

    public IndexPriceResponseDTO findLastIndexPriceById(Integer id) {
        return IndexInfoResponseDTO.entityToDTO(indexInfoRepository.findById(id).orElse(null)).getIndexPriceList().stream()
                .max(Comparator.comparing(IndexPriceResponseDTO::getBaseDate))
                .orElse(null);
    }
}
