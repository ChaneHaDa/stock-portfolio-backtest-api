package com.chan.stock_portfolio_backtest_api.domain;

import com.chan.stock_portfolio_backtest_api.constants.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    @Column
    private String name;
    @Column
    private String phoneNumber;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime lastLoginAt;
    @Column
    @Builder.Default
    private Boolean isActive = true;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
