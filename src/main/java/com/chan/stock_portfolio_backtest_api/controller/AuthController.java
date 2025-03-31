package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.request.LoginRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.UsersRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.UsersResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.UsersService;
import com.chan.stock_portfolio_backtest_api.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth API", description = "인증, 인가 API")
public class AuthController {
    private final UsersService usersService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UsersService usersService, AuthenticationManager authenticationManager) {
        this.usersService = usersService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(summary = "회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "401", description = "회원 가입 실패")
    })
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<UsersResponseDTO>> registerUser(
            @RequestBody UsersRequestDTO usersRequestDTO
    ) {
        UsersResponseDTO createdUser = usersService.createUser(usersRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(ResponseDTO.<UsersResponseDTO>builder()
                        .status("success")
                        .message("회원가입 성공")
                        .data(createdUser)
                        .build());
    }

    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getId(),
                        loginRequestDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JWTUtil.createToken(authentication.getName(), authentication.getAuthorities());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(ResponseDTO.builder()
                        .status("success")
                        .message("로그인 성공")
                        .data(Map.of("accessToken", token))
                        .build());
    }

    @Operation(summary = "아이디 중복 체크", description = "사용 가능한 아이디인지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이디 사용 가능"),
            @ApiResponse(responseCode = "409", description = "아이디 중복됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @GetMapping("/check-username")
    public ResponseEntity<ResponseDTO<String>> checkUsername(
            @RequestParam("username")
            @NotBlank(message = "아이디는 필수 입력값입니다.") String username
    ) {
        boolean isAvailable = usersService.isUsernameAvailable(username);

        if (isAvailable) {
            ResponseDTO<String> response = ResponseDTO.<String>builder()
                    .status("success")
                    .message("The username is available.")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ResponseDTO<String> response = ResponseDTO.<String>builder()
                    .status("fail")
                    .message("The username is already taken.")
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @Operation(summary = "이메일 중복 체크", description = "사용 가능한 이메일인지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 사용 가능"),
            @ApiResponse(responseCode = "409", description = "이메일 중복됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @GetMapping("/check-email")
    public ResponseEntity<ResponseDTO<String>> checkEmail(
            @RequestParam("email")
            @NotBlank(message = "이메일는 필수 입력값입니다.") String email) {
        boolean isAvailable = usersService.isEmailAvailable(email);
        if (isAvailable) {
            ResponseDTO<String> response = ResponseDTO.<String>builder()
                    .status("success")
                    .message("The email is available.")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ResponseDTO<String> response = ResponseDTO.<String>builder()
                    .status("fail")
                    .message("The email is already taken.")
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @Operation(summary = "이메일 인증 요청", description = "사용자가 입력한 이메일로 인증 토큰을 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 이메일 발송 성공"),
            @ApiResponse(responseCode = "409", description = "이메일이 이미 등록되었거나 인증 중임"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값")
    })
    @GetMapping("/initiate-email")
    public ResponseEntity<ResponseDTO<String>> initiateEmail(
            @RequestParam("email") @NotBlank(message = "이메일은 필수 입력값입니다.") String email) {
        try {
            usersService.requestEmailVerification(email);
            ResponseDTO<String> response = ResponseDTO.<String>builder()
                    .status("success")
                    .message("인증 이메일 발송이 완료되었습니다.")
                    .data(email)
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseDTO<String> errorResponse = ResponseDTO.<String>builder()
                    .status("error")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            ResponseDTO<String> errorResponse = ResponseDTO.<String>builder()
                    .status("error")
                    .message("서버 내부 오류 발생")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
