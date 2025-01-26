package com.chan.stock_portfolio_backtest_api.dto.response;

import com.chan.stock_portfolio_backtest_api.domain.Users;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 외부에서 생성자 호출 방지
public class UsersResponseDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final String name;
    private final String phoneNumber;

    public static UsersResponseDTO fromEntity(Users users) {
        return new UsersResponseDTO(
                users.getId(),
                users.getUsername(),
                users.getEmail(),
                users.getName(),
                users.getPhoneNumber()
        );
    }
}
