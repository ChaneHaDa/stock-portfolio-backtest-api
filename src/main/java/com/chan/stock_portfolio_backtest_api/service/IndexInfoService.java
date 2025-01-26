package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.dto.request.IndexInfoRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.IndexPriceRequestDTO;
import com.chan.stock_portfolio_backtest_api.repository.IndexInfoRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class IndexInfoService {
    private final IndexInfoRepository indexInfoRepository;

    public IndexInfoService(IndexInfoRepository indexInfoRepository) {
        this.indexInfoRepository = indexInfoRepository;
    }

    public IndexInfoRequestDTO findIndexInfoByName(String name) {
        return IndexInfoRequestDTO.entityToDTO(indexInfoRepository.findByName(name));
    }

    public IndexPriceRequestDTO findLastIndexPriceByName(String name) {
        return IndexInfoRequestDTO.entityToDTO(indexInfoRepository.findByName(name)).getIndexPriceList().stream()
                .max(Comparator.comparing(IndexPriceRequestDTO::getBaseDate))
                .orElse(null);
    }
}
