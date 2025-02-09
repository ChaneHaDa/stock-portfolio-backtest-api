package com.chan.stock_portfolio_backtest_api.dto.request;

import com.chan.stock_portfolio_backtest_api.domain.IndexInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexInfoRequestDTO {

    private Integer id;
    private String name;
    private String category;
    private List<IndexPriceRequestDTO> indexPriceList;

    public static IndexInfoRequestDTO entityToDTO(IndexInfo indexInfo) {
        return new IndexInfoRequestDTO(indexInfo.getId(), indexInfo.getName(), indexInfo.getCategory(),
                indexInfo.getIndexPriceList().stream().map(IndexPriceRequestDTO::entityToDTO).toList());
    }
}
