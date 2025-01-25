package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.IndexInfoDTO;
import com.chan.stock_portfolio_backtest_api.dto.IndexPriceDTO;
import com.chan.stock_portfolio_backtest_api.service.IndexInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/index")
public class IndexPriceController {
    private final IndexInfoService indexInfoService;

    public IndexPriceController(IndexInfoService indexInfoService) {
        this.indexInfoService = indexInfoService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<IndexInfoDTO> getIndex(@PathVariable("name") String name) {
        IndexInfoDTO indexInfoDTO = indexInfoService.findIndexInfoByName(name);
        return ResponseEntity.ok().body(indexInfoDTO);
    }

    @GetMapping("/{name}/price")
    public ResponseEntity<IndexPriceDTO> getLastIndexPrice(@PathVariable("name") String name) {
        IndexPriceDTO indexPriceDTO = indexInfoService.findLastIndexPriceByName(name);
        return ResponseEntity.ok().body(indexPriceDTO);
    }

}
