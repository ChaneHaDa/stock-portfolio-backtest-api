package com.chan.stock_portfolio_backtest_api.dto;

import com.chan.stock_portfolio_backtest_api.domain.Users;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 외부에서 생성자 호출 방지
public class UsersDTO {
    private final String username;
    private final String email;
    private final String name;
    private final String phoneNumber;

    public static UsersDTO fromEntity(Users users) {
        return new UsersDTO(
                users.getUsername(),
                users.getEmail(),
                users.getName(),
                users.getPhoneNumber()
        );
    }
}
