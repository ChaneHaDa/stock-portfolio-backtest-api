package com.chan.stock_portfolio_backtest_api.db.service;

import com.chan.stock_portfolio_backtest_api.constants.Role;
import com.chan.stock_portfolio_backtest_api.db.dto.UsersDTO;
import com.chan.stock_portfolio_backtest_api.db.entity.Users;
import com.chan.stock_portfolio_backtest_api.db.repository.UsersRepository;
import com.chan.stock_portfolio_backtest_api.dto.input.RegisterInputDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsersDTO createUser(RegisterInputDTO registerInputDTO) {
        String password = passwordEncoder.encode(registerInputDTO.getPassword());
        Users user = new Users(null, registerInputDTO.getUsername(), password,
                registerInputDTO.getEmail(), registerInputDTO.getName(), registerInputDTO.getPhoneNumber(),
                LocalDateTime.now(), null, true, Role.USER
        );
        Users savedUsers = usersRepository.save(user);

        return UsersDTO.fromEntity(savedUsers);
    }
}
