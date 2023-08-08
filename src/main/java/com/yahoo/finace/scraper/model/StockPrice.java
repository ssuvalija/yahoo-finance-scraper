package com.yahoo.finace.scraper.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_price")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long stockPriceId;

    @ManyToOne
    @JoinColumn(name = "tickerId")
    private Ticker ticker;

    private LocalDateTime dateTime;
    private double previousClosePrice;
    private double openPrice;
}
