package com.chan.stock_portfolio_backtest_api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDTO<T> {
    private String status;
    private String code;
    private String message;
    private T data;
}