package com.chan.stock_portfolio_backtest_api;

import com.chan.stock_portfolio_backtest_api.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {
    @Autowired
    private EmailService emailService;

    @Test
    public void sendVerificationEmail() {
        emailService.sendVerificationEmail("xoo0608@naver.com", "token!!");
    }
}
