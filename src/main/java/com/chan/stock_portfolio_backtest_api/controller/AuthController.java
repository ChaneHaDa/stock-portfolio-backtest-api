package com.chan.stock_portfolio_backtest_api.controller;

import com.chan.stock_portfolio_backtest_api.dto.request.LoginRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.UsersRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.UsersResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.UsersService;
import com.chan.stock_portfolio_backtest_api.util.JWTUtil;
import com.chan.stock_portfolio_backtest_api.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다. 이메일 인증이 완료된 상태여야 합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "회원 가입 성공",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"success\",\"message\":\"회원가입 성공\",\"data\":{\"id\":1,\"username\":\"testuser\",\"email\":\"test@example.com\"}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "사용자 중복 (아이디 또는 이메일)",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"error\",\"code\":\"USER_ALREADY_EXISTS\",\"message\":\"이미 사용 중인 아이디입니다.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이메일 인증 미완료",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"error\",\"code\":\"BAD_REQUEST\",\"message\":\"이메일 인증 이전 입니다.\"}"
                            )
                    )
            )
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
                .body(ResponseUtil.success(createdUser, "회원가입 성공"));
    }

    @Operation(
            summary = "로그인",
            description = "사용자 인증을 통해 JWT 토큰을 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"success\",\"message\":\"로그인 성공\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzI1NiJ9...\"}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"error\",\"code\":\"AUTH_FAILED\",\"message\":\"아이디/비밀번호가 일치하지 않습니다.\"}"
                            )
                    )
            )
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
                .body(ResponseUtil.success(Map.of("accessToken", token), "로그인 성공"));
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
            return ResponseEntity.ok(ResponseUtil.success(null, "The username is available."));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDTO.<String>builder()
                            .status("fail")
                            .message("The username is already taken.")
                            .build());
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
            return ResponseEntity.ok(ResponseUtil.success(null, "The email is available."));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDTO.<String>builder()
                            .status("fail")
                            .message("The email is already taken.")
                            .build());
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
        usersService.requestEmailVerification(email);
        return ResponseEntity.ok(ResponseUtil.success(email, "인증 이메일 발송이 완료되었습니다."));
    }

    @Operation(summary = "이메일 인증 토큰 검증", description = "사용자가 입력한 이메일과 인증 토큰을 검증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 토큰 확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값")
    })
    @GetMapping("/verify-email")
    public ResponseEntity<ResponseDTO<String>> verifyEmail(
            @RequestParam("email") @NotBlank(message = "이메일은 필수 입력값입니다.") String email,
            @RequestParam("token") @NotBlank(message = "토큰은 필수 입력값입니다.") String token) {
        usersService.emailValidation(email, token);
        return ResponseEntity.ok(ResponseUtil.success(null, "The email is validated."));
    }

}
