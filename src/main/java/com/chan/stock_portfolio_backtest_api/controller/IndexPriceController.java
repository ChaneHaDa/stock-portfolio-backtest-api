package com.chan.stock_portfolio_backtest_api.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chan.stock_portfolio_backtest_api.dto.response.ResponseDTO;
import com.chan.stock_portfolio_backtest_api.service.IndexPriceService;
import com.chan.stock_portfolio_backtest_api.util.ResponseUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/v1/indexs/{index_id}/prices")
@Tag(name = "Index Price API", description = "지수 가격 정보 조회 API")
public class IndexPriceController {
	private final IndexPriceService indexPriceService;

	public IndexPriceController(IndexPriceService indexPriceService) {
		this.indexPriceService = indexPriceService;
	}

	@Operation(
		summary = "지수 가격 조회",
		description = "지수 ID로 해당 지수의 가격 정보를 조회합니다. 기간 설정시 해당 기간의 가격 데이터를 반환합니다. 페이징 파라미터를 사용하여 대량 데이터를 효율적으로 조회할 수 있습니다."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "조회 성공",
			content = @Content(
				schema = @Schema(implementation = ResponseDTO.class),
				examples = @ExampleObject(
					value = "{\"status\":\"success\",\"data\":{\"content\":[{\"id\":1,\"closePrice\":2500.5,\"openPrice\":2480.3,\"lowPrice\":2470.1,\"highPrice\":2510.8,\"yearlyDiff\":5.2,\"baseDate\":\"2024-01-01\"}],\"totalElements\":2500,\"totalPages\":25,\"size\":100,\"number\":0,\"first\":true,\"last\":false}}"
				)
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "지수 정보 없음",
			content = @Content(
				schema = @Schema(implementation = ResponseDTO.class),
				examples = @ExampleObject(
					value = "{\"status\":\"error\",\"code\":\"ENTITY_NOT_FOUND\",\"message\":\"1 not found\"}"
				)
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = "잘못된 요청 파라미터",
			content = @Content(
				schema = @Schema(implementation = ResponseDTO.class),
				examples = @ExampleObject(
					value = "{\"status\":\"error\",\"code\":\"BAD_REQUEST\",\"message\":\"Start date must not be after end date.\"}"
				)
			)
		)
	})
	@GetMapping
	public ResponseEntity<ResponseDTO<?>> getIndexPriceByIndexId(
		@PathVariable(name = "index_id")
		@NotNull(message = "index_id는 필수입니다.") Integer indexId,
		@RequestParam(value = "startDate", required = false)
		@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
		@RequestParam(value = "endDate", required = false)
		@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
		@RequestParam(value = "page", defaultValue = "0") Integer page,
		@RequestParam(value = "size", defaultValue = "100") Integer size,
		@RequestParam(value = "sort", defaultValue = "baseDate") String sort,
		@RequestParam(value = "direction", defaultValue = "DESC") String direction
	) {
		// 페이지 번호 및 사이즈 검증
		if (page < 0) {
			throw new IllegalArgumentException("Page number must not be negative.");
		}
		if (size <= 0 || size > 1000) {
			throw new IllegalArgumentException("Page size must be between 1 and 1000.");
		}

		if (startDate != null && endDate != null) {
			return ResponseEntity.ok(ResponseUtil.success(
				indexPriceService.findIndexPricesByIndexInfoIdAndDateRangeWithPaging(
					indexId, startDate, endDate, page, size, sort, direction)));
		} else {
			return ResponseEntity.ok(ResponseUtil.success(
				indexPriceService.findIndexPricesByIndexInfoIdWithPaging(
					indexId, page, size, sort, direction)));
		}
	}
}
