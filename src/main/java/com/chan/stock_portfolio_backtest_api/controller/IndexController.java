package com.chan.stock_portfolio_backtest_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chan.stock_portfolio_backtest_api.dto.request.IndexBacktestRequestDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.IndexBacktestResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.IndexInfoResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.IndexSearchResponseDTO;
import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.exception.EntityNotFoundException;
import com.chan.stock_portfolio_backtest_api.service.IndexBacktestService;
import com.chan.stock_portfolio_backtest_api.service.IndexInfoService;
import com.chan.stock_portfolio_backtest_api.util.ResponseUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/v1/indexs")
@Tag(name = "Index API", description = "지수 정보 조회 API")
@Validated
public class IndexController {
	private final IndexInfoService indexInfoService;
	private final IndexBacktestService indexBacktestService;

	public IndexController(IndexInfoService indexInfoService, IndexBacktestService indexBacktestService) {
		this.indexInfoService = indexInfoService;
		this.indexBacktestService = indexBacktestService;
	}

	@Operation(summary = "지수 목록 조회", description = "쿼리로 지수 목록 조회")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 입력값"),
		@ApiResponse(responseCode = "404", description = "지수 정보 없음")
	})
	@GetMapping
	public ResponseEntity<ResponseDTO<?>> getIndexesByParams(
		@RequestParam(value = "name", required = false) String name,
		@RequestParam(value = "category", required = false) String category,
		@RequestParam(value = "q", required = false) String q
	) {
		if (name == null && category == null && q == null) {
			throw new EntityNotFoundException("Index를 찾을 수 없습니다.");
		}

		if (q != null) {
			List<IndexSearchResponseDTO> indexSearchResponseDTOList = indexInfoService.findIndexesByQuery(q.trim());
			return ResponseEntity.ok(ResponseUtil.success(indexSearchResponseDTOList));
		} else {
			List<IndexInfoResponseDTO> indexInfoResponseDTOList = indexInfoService.findIndexesByParams(name, category);
			return ResponseEntity.ok(ResponseUtil.success(indexInfoResponseDTOList));
		}
	}

	@Operation(summary = "특정 지수 기본 정보 조회", description = "id로 지수 상세 조회")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "404", description = "지수 정보 없음"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
	})
	@GetMapping("/{id}")
	public ResponseEntity<ResponseDTO<IndexInfoResponseDTO>> getIndex(
		@PathVariable("id")
		@NotNull(message = "id는 필수 입력값입니다.") Integer id
	) {
		IndexInfoResponseDTO indexInfoResponseDTO = indexInfoService.findIndexInfoById(id);
		return ResponseEntity.ok(ResponseUtil.success(indexInfoResponseDTO));
	}

	@Operation(summary = "지수 기반 백테스트")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "백테스트 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "404", description = "지수 정보 없음")
	})
	@GetMapping("/{id}/portfolios")
	public ResponseEntity<ResponseDTO<IndexBacktestResponseDTO>> getIndexPortfolio(
		@PathVariable("id")
		@NotNull(message = "id는 필수 입력값입니다.") Integer id,
		@ModelAttribute @Valid IndexBacktestRequestDTO indexBacktestRequestDTO
	) {
		IndexBacktestResponseDTO backtestResult = indexBacktestService.calculateIndexBacktest(indexBacktestRequestDTO,
			id);
		return ResponseEntity.ok(ResponseUtil.success(backtestResult));
	}

}
