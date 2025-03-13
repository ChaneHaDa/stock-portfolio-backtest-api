package com.chan.stock_portfolio_backtest_api.dto.response;

import com.chan.stock_portfolio_backtest_api.domain.Users;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UsersResponseDTO {
    private final Integer id;
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
