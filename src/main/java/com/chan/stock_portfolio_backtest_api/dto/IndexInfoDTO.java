package com.chan.stock_portfolio_backtest_api.dto;

import com.chan.stock_portfolio_backtest_api.domain.IndexInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfoDTO {

    private Integer id;
    private String name;
    private String category;
    private List<IndexPriceDTO> indexPriceList;

    public static IndexInfoDTO entityToDTO(IndexInfo indexInfo) {
        return new IndexInfoDTO(indexInfo.getId(), indexInfo.getName(), indexInfo.getCategory(),
                indexInfo.getIndexPriceList().stream().map(IndexPriceDTO::entityToDTO).toList());
    }
}
