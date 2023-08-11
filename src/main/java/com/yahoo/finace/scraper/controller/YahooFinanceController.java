package com.yahoo.finace.scraper.controller;

import com.yahoo.finace.scraper.dto.ApiResponse;
import com.yahoo.finace.scraper.dto.GetStockPricesRequestDto;
import com.yahoo.finace.scraper.dto.TickerResponseDto;
import com.yahoo.finace.scraper.service.YahooFinanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public List<String> getTrendingTickers() {
        // Placeholder logic: Call the service to fetch trending tickers
        return yahooFinanceService.getTrendingTickers();
    }

    @GetMapping("/api/stock-data/{ticker}")
    public TickerResponseDto getLatestFinancialData(@PathVariable String ticker) {
        try {
            return yahooFinanceService.getLatestFinancialData(ticker);
        } catch (IOException e) {
            //TODO: add aspect for handling exceptions
            throw new RuntimeException(e);
        }
    }
}