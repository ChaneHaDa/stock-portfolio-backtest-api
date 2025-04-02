package com.chan.stock_portfolio_backtest_api.exception;

import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 1. 유효성 검사 실패 (400)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        ResponseDTO<Map<String, String>> response = ResponseDTO.<Map<String, String>>builder()
                .status("error")
                .code("VALIDATION_FAILED")
                .message("입력값 검증 실패")
                .data(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // 2. 잘못된 요청 본문 (400)
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                .status("error")
                .code("INVALID_REQUEST")
                .message("잘못된 요청 형식입니다.")
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // 3. 인증 실패 (401)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDTO<Void>> handleBadCredentials(BadCredentialsException ex) {
        ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                .status("error")
                .code("AUTH_FAILED")
                .message("아이디/비밀번호가 일치하지 않습니다.")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 4. 엔티티 없음 (404)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseDTO<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                .status("error")
                .code("ENTITY_NOT_FOUND")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 5. 기타 서버 오류 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Void>> handleAll(Exception ex) {
        logger.error("서버 오류 발생: ", ex);

        ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                .status("error")
                .code("INTERNAL_ERROR")
                .message("서버 내부 오류가 발생했습니다.")
                .build();

        return ResponseEntity.internalServerError().body(response);
    }

    // 6. 인증 오류 (400)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDTO<Void>> handleBadRequestException(Exception ex) {
        logger.error("인증 오류 발생: ", ex);

        ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                .status("error")
                .code("BAD_REQUEST")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}