package com.chan.stock_portfolio_backtest_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chan.stock_portfolio_backtest_api.domain.Portfolio;
import com.chan.stock_portfolio_backtest_api.domain.PortfolioItem;
import com.chan.stock_portfolio_backtest_api.domain.Stock;
import com.chan.stock_portfolio_backtest_api.domain.Users;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioItemRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.request.PortfolioRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioDetailResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.PortfolioResponseDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.exception.PortfolioNotFoundException;
import com.chan.stock_portfolio_backtest_api.exception.StockNotFoundException;
import com.chan.stock_portfolio_backtest_api.constants.AppConstants;
import com.chan.stock_portfolio_backtest_api.repository.PortfolioRepository;
import com.chan.stock_portfolio_backtest_api.repository.StockRepository;

@Service
public class PortfolioService {

	private final PortfolioRepository portfolioRepository;
	private final AuthService authService;
	private final StockRepository stockRepository;
	private final SecurityService securityService;

	public PortfolioService(PortfolioRepository portfolioRepository, AuthService authService,
		StockRepository stockRepository, SecurityService securityService) {
		this.portfolioRepository = portfolioRepository;
		this.authService = authService;
		this.stockRepository = stockRepository;
		this.securityService = securityService;
	}

	// save
	public PortfolioResponseDTO createPortfolio(PortfolioRequestDTO portfolioRequestDTO) {
		Users user = authService.getCurrentUser();
		Portfolio portfolio = Portfolio.builder()
			.name(portfolioRequestDTO.getName())
			.description(portfolioRequestDTO.getDescription())
			.ror(portfolioRequestDTO.getRor())
			.amount(portfolioRequestDTO.getAmount())
			.price(portfolioRequestDTO.getPrice())
			.startDate(portfolioRequestDTO.getStartDate())
			.endDate(portfolioRequestDTO.getEndDate())
			.volatility(portfolioRequestDTO.getVolatility())
			.user(user)
			.build();

		portfolioRequestDTO.getPortfolioItemRequestDTOList().forEach(item -> {
			Stock stock = stockRepository.findById(item.getStockId()).orElseThrow(EntityNotFoundException::new);
			portfolio.addPortfolioItem(PortfolioItemRequestDTO.DTOToEntity(item, stock));
		});

		return PortfolioResponseDTO.entityToDTO(portfolioRepository.save(portfolio));
	}

	// find: By User, 현재 로그인한 유저의 포트폴리오 리스트 반환
	public List<PortfolioResponseDTO> findPortfolioByUser() {
		Users user = authService.getCurrentUser();
		List<PortfolioResponseDTO> portfolioResponseDTOList = portfolioRepository.findAllByUser(user)
			.stream()
			.map(PortfolioResponseDTO::entityToDTO)
			.toList();

		if (portfolioResponseDTOList.isEmpty()) {
			throw new EntityNotFoundException(AppConstants.ENTITY_NOT_FOUND_ERROR);
		}

		return portfolioResponseDTOList;
	}

	// find: By Id, Id로 포트폴리오 상세 반환
	public PortfolioDetailResponseDTO findPortfolioById(Integer id) {
		Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(EntityNotFoundException::new);

		return PortfolioDetailResponseDTO.entityToDTO(portfolio);
	}

	// delete
	public void deletePortfolio(Integer id) {
		Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		Users user = authService.getCurrentUser();
		securityService.validatePortfolioOwnership(portfolio, user);
		portfolioRepository.delete(portfolio);
	}

	@Transactional
	public PortfolioResponseDTO updatePortfolio(Integer portfolioId, PortfolioRequestDTO portfolioRequestDTO) {
		Portfolio portfolio = portfolioRepository.findById(portfolioId)
			.orElseThrow(() -> new PortfolioNotFoundException(portfolioId));

		portfolio.updatePortfolio(
			portfolioRequestDTO.getName(),
			portfolioRequestDTO.getDescription(),
			portfolioRequestDTO.getAmount(),
			portfolioRequestDTO.getStartDate(),
			portfolioRequestDTO.getEndDate(),
			portfolioRequestDTO.getRor(),
			portfolioRequestDTO.getVolatility(),
			portfolioRequestDTO.getPrice()
		);

		if (portfolioRequestDTO.getPortfolioItemRequestDTOList() != null) {
			List<PortfolioItem> newItems = new ArrayList<>();
			for (PortfolioItemRequestDTO itemDTO : portfolioRequestDTO.getPortfolioItemRequestDTOList()) {
				Stock stock = stockRepository.findById(itemDTO.getStockId())
					.orElseThrow(() -> new StockNotFoundException(itemDTO.getStockId()));
				PortfolioItem newItem = PortfolioItemRequestDTO.DTOToEntity(itemDTO, stock);
				newItems.add(newItem);
			}
			portfolio.updatePortfolioItems(newItems);
		}

		return PortfolioResponseDTO.entityToDTO(portfolio);
	}
}
