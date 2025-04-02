package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.constants.Role;
import com.chan.stock_portfolio_backtest_api.data.EmailVerificationQueue;
import com.chan.stock_portfolio_backtest_api.domain.Users;
import com.chan.stock_portfolio_backtest_api.dto.request.UsersRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.UsersResponseDTO;
import com.chan.stock_portfolio_backtest_api.exception.BadRequestException;
import com.chan.stock_portfolio_backtest_api.exception.UserAlreadyExistsException;
import com.chan.stock_portfolio_backtest_api.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private EmailVerificationQueue verificationQueue = new EmailVerificationQueue();


    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public UsersResponseDTO createUser(UsersRequestDTO dto) {
        if (usersRepository.existsByUsername(dto.getUsername())) {
            throw new UserAlreadyExistsException("이미 사용 중인 아이디입니다.");
        }
        if (usersRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("이미 등록된 이메일입니다.");
        }
        if (!verificationQueue.isVerify(dto.getEmail())) {
            throw new BadRequestException("이메일 인증 이전 입니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        Users user = Users.builder()
                .username(dto.getUsername())
                .password(encodedPassword)
                .email(dto.getEmail())
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .role(Role.USER)
                .build();

        Users savedUser = usersRepository.save(user);

        return UsersResponseDTO.fromEntity(savedUser);
    }

    public Users getUsers(String username) {
        return usersRepository.findByUsername(username);
    }

    public Boolean isUsernameAvailable(String username) {
        return !usersRepository.existsByUsername(username);
    }

    public Boolean isEmailAvailable(String email) {
        return !usersRepository.existsByEmail(email) && !verificationQueue.containsEmail(email);
    }

    public void requestEmailVerification(String email) {
        if (!isEmailAvailable(email)) {
            throw new IllegalArgumentException("이미 등록되었거나 인증 중인 이메일입니다.");
        }

        String token = UUID.randomUUID().toString();
        verificationQueue.add(email, token, 5);

        emailService.sendVerificationEmail(email, token);
    }

    public boolean isEmailValid(String email) {
        return verificationQueue.isVerify(email);
    }

    public void emailValidation(String email, String token) {
        verificationQueue.verify(email, token);
    }

}