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
        return IndexInfoRequestDTO.builder()
                .id(indexInfo.getId())
                .name(indexInfo.getName())
                .category(indexInfo.getCategory())
                .indexPriceList(indexInfo.getIndexPriceList().stream().map(IndexPriceRequestDTO::entityToDTO).toList())
                .build();
    }
}
