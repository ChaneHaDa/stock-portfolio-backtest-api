package com.chan.stock_portfolio_backtest_api.db.dto;

import com.chan.stock_portfolio_backtest_api.db.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {
    private String username;
    private String email;
    private String name;
    private String phoneNumber;

    public static UsersDTO fromEntity(Users users) {
        return new UsersDTO(users.getUsername(), users.getEmail(), users.getName(), users.getPhoneNumber());
    }
}
