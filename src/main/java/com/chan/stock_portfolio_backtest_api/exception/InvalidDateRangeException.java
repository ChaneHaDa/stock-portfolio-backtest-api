package com.chan.stock_portfolio_backtest_api.exception;

public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException() {
        super("Invalid date range: start date must not be after end date");
    }
    
    public InvalidDateRangeException(String message) {
        super(message);
    }
}