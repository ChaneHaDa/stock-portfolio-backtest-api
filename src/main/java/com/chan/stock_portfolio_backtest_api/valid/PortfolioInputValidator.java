package com.chan.stock_portfolio_backtest_api.valid;

import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestItemDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.util.List;

public class PortfolioInputValidator implements ConstraintValidator<ValidPortfolioInput, PortfolioRequestDTO> {
    @Override
    public boolean isValid(PortfolioRequestDTO portfolioRequestDTO, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        if (portfolioRequestDTO == null) {
            return true;
        }

        //startDate < endDate valid
        LocalDate startDate = portfolioRequestDTO.getStartDate();
        LocalDate endDate = portfolioRequestDTO.getEndDate();
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
        List<PortfolioRequestItemDTO> portfolioRequestItemDTOList = portfolioRequestDTO.getPortfolioRequestItemDTOList();
        float weightSum = 0;
        for (PortfolioRequestItemDTO i : portfolioRequestItemDTOList) {
            weightSum += i.getWeight();
        }

        if (weightSum != 1) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("weight sum is might be 1")
                    .addPropertyNode("portfolioInputItemDTOList")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
