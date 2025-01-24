package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.db.service.UsersService;
import com.chan.stock_portfolio_backtest_api.dto.input.LoginDTO;
import com.chan.stock_portfolio_backtest_api.dto.input.RegisterInputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UsersService usersService;

    public AuthController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterInputDTO registerInputDTO) {
        return ResponseEntity.ok().body(usersService.createUser(registerInputDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok().body("aaa");
    }

}
