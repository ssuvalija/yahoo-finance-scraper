package com.yahoo.finance.scraper.controller;

import com.yahoo.finance.scraper.dto.ApiResponse;
import com.yahoo.finance.scraper.dto.GetStockPricesRequestDto;
import com.yahoo.finance.scraper.dto.TickerResponseDto;
import com.yahoo.finance.scraper.service.YahooFinanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class YahooFinanceController {

    private final YahooFinanceService yahooFinanceService;

    @Autowired
    public YahooFinanceController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    @PostMapping("/api/stock-data")
    public ResponseEntity<ApiResponse<List<TickerResponseDto>>> getStockData(
            @RequestBody @Valid GetStockPricesRequestDto request,
            BindingResult bindingResult) {
        ApiResponse<List<TickerResponseDto>> response = new ApiResponse<>();

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            response.setSuccess(false);
            response.setErrors(errors);

            return ResponseEntity.badRequest().body(response);
        }

        List<TickerResponseDto> tickerDtos = yahooFinanceService.getTickersAndStockPrices(request.getTickers(), request.getDate());
        response.setSuccess(true);
        response.setData(tickerDtos);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/trending-tickers")
    public ResponseEntity<ApiResponse<List<String>>> getTrendingTickers() {
        // Placeholder logic: Call the service to fetch trending tickers
        ApiResponse<List<String>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(yahooFinanceService.getTrendingTickers());

        return  ResponseEntity.ok(response);

    }

    @GetMapping("/api/stock-data/{ticker}")
    public ResponseEntity<ApiResponse<TickerResponseDto>> getLatestFinancialData(@PathVariable String ticker) {
        ApiResponse<TickerResponseDto> response = new ApiResponse<>();
        try {
            TickerResponseDto tickerResponseDto = yahooFinanceService.getLatestFinancialData(ticker);
            response.setData(tickerResponseDto);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.setSuccess(false);
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            response.setErrors(errors);

            return ResponseEntity.internalServerError().body(response);
        }
    }
}