package com.chan.stock_portfolio_backtest_api.service;

import com.chan.stock_portfolio_backtest_api.domain.Portfolio;
import com.chan.stock_portfolio_backtest_api.domain.Users;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.constants.AppConstants;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    /**
     * 현재 사용자가 해당 포트폴리오의 소유자인지 확인
     * 
     * @param portfolio 확인할 포트폴리오
     * @param currentUser 현재 로그인한 사용자
     * @throws EntityNotFoundException 소유자가 아닌 경우
     */
    public void validatePortfolioOwnership(Portfolio portfolio, Users currentUser) {
        if (!portfolio.getUser().getId().equals(currentUser.getId())) {
            throw new EntityNotFoundException(AppConstants.ENTITY_NOT_FOUND_ERROR);
        }
    }
}