package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.db.service.UsersService;
import com.chan.stock_portfolio_backtest_api.dto.input.RegisterInputDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterInputDTO registerInputDTO) {
        return ResponseEntity.ok().body(usersService.createUser(registerInputDTO));
    }
}
