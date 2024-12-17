package com.chan.stock_portfolio_backtest_api.db.service;

import com.chan.stock_portfolio_backtest_api.db.dto.IndexInfoDTO;
import com.chan.stock_portfolio_backtest_api.db.repository.IndexInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class IndexInfoService {
    private final IndexInfoRepository indexInfoRepository;

    public IndexInfoService(IndexInfoRepository indexInfoRepository) {
        this.indexInfoRepository = indexInfoRepository;
    }

    public IndexInfoDTO findIndexInfoByName(String name) {
        return IndexInfoDTO.entityToDTO(indexInfoRepository.findByName(name));
    }
}
