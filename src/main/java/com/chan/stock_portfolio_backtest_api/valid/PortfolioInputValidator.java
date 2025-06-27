package com.chan.stock_portfolio_backtest_api.valid;

import java.time.LocalDate;
import java.util.List;

import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioBacktestRequestItemDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PortfolioInputValidator implements ConstraintValidator<ValidPortfolioInput, PortfolioBacktestRequestDTO> {
	@Override
	public boolean isValid(PortfolioBacktestRequestDTO portfolioBacktestRequestDTO,
		ConstraintValidatorContext constraintValidatorContext) {
		constraintValidatorContext.disableDefaultConstraintViolation();

		if (portfolioBacktestRequestDTO == null) {
			return true;
		}

		//startDate < endDate valid
		LocalDate startDate = portfolioBacktestRequestDTO.getStartDate();
		LocalDate endDate = portfolioBacktestRequestDTO.getEndDate();
		if (startDate != null && endDate != null) {
			if (!startDate.isBefore(endDate)) {
				constraintValidatorContext
					.buildConstraintViolationWithTemplate("endDate must be after the startDate")
					.addPropertyNode("endDate")
					.addConstraintViolation();
				return false;
			}
		}

		//weight sum == 1 valid
		List<PortfolioBacktestRequestItemDTO> portfolioBacktestRequestItemDTOList = portfolioBacktestRequestDTO.getPortfolioBacktestRequestItemDTOList();
		float weightSum = 0;
		for (PortfolioBacktestRequestItemDTO i : portfolioBacktestRequestItemDTOList) {
			weightSum += i.getWeight();
		}

		if (weightSum != 1) {
			constraintValidatorContext
				.buildConstraintViolationWithTemplate("weight sum is might be 1")
				.addPropertyNode("portfolioBacktestRequestItemDTOList")
				.addConstraintViolation();
			return false;
		}

		return true;
	}
}
