package com.chan.stock_portfolio_backtest_api.dto.response;

import com.chan.stock_portfolio_backtest_api.domain.IndexInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexInfoResponseDTO {
    private Integer id;
    private String name;
    private String category;

    public static IndexInfoResponseDTO entityToDTO(IndexInfo indexInfo) {
        return IndexInfoResponseDTO.builder()
                .id(indexInfo.getId())
                .name(indexInfo.getName())
                .category(indexInfo.getCategory())
                .build();
    }
}
