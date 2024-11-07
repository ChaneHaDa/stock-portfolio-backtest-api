package com.chan.stock_portfolio_backtest_api.valid;

import com.chan.stock_portfolio_backtest_api.dto.PortfolioInputItemDTO;
import com.chan.stock_portfolio_backtest_api.dto.PortfolionputDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.List;

public class PortfolioInputValidator implements ConstraintValidator<ValidPortfolioInput, PortfolionputDTO> {
    @Override
    public boolean isValid(PortfolionputDTO portfolionputDTO, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        if (portfolionputDTO == null) {
            return true;
        }

        //startDate < endDate valid
        LocalDate startDate = portfolionputDTO.getStartDate();
        LocalDate endDate = portfolionputDTO.getEndDate();
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
        List<PortfolioInputItemDTO> portfolioInputItemDTOList = portfolionputDTO.getPortfolioInputItemDTOList();
        float weightSum = 0;
        for (PortfolioInputItemDTO i : portfolioInputItemDTOList) {
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
