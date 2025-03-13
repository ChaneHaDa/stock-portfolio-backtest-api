package com.chan.stock_portfolio_backtest_api.dto.response;

import com.chan.stock_portfolio_backtest_api.domain.IndexInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexInfoResponseDTO {
    private Integer id;
    private String name;
    private String category;
    private List<IndexPriceResponseDTO> indexPriceList;

    public static IndexInfoResponseDTO entityToDTO(IndexInfo indexInfo) {
        return IndexInfoResponseDTO.builder()
                .id(indexInfo.getId())
                .name(indexInfo.getName())
                .category(indexInfo.getCategory())
                .indexPriceList(indexInfo.getIndexPriceList().stream().map(IndexPriceResponseDTO::entityToDTO).toList())
                .build();
    }
}
