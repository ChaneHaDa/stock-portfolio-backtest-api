package com.chan.stock_portfolio_backtest_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String id;
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
