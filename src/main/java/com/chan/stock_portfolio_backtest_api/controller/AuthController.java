package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.UsersDTO;
import com.chan.stock_portfolio_backtest_api.service.UsersService;
import com.chan.stock_portfolio_backtest_api.dto.request.LoginDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.RegisterInputDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.util.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UsersService usersService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UsersService usersService, AuthenticationManager authenticationManager) {
        this.usersService = usersService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<UsersDTO>> registerUser(
            @Valid @RequestBody RegisterInputDTO registerInputDTO // 유효성 검사 추가
    ) {
        UsersDTO createdUser = usersService.createUser(registerInputDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity
                .created(location) // 201 Created
                .body(ResponseDTO.<UsersDTO>builder()
                        .status("success")
                        .message("회원가입 성공") // 메시지 수정
                        .data(createdUser)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getId(),
                        loginDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JWTUtil.createToken(authentication.getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(ResponseDTO.builder()
                        .status("success")
                        .message("로그인 성공")
                        .data(Map.of("accessToken", token))
                        .build());
    }

}
