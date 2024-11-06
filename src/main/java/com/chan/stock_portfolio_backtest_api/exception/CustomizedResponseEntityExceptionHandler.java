package com.chan.stock_portfolio_backtest_api.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) throws Exception {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(Exception ex, WebRequest request)
            throws Exception {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public final ResponseEntity<ErrorDetails> handleDateTimeParseException(Exception ex) throws Exception {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "Date input error", ex.getMessage());
        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.BAD_REQUEST);
    }

}
