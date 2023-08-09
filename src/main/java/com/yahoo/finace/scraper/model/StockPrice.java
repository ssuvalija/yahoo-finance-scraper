package com.yahoo.finace.scraper.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_price")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockPrice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long stockPriceId;

    @ManyToOne
    @JoinColumn(name = "tickerId")
    private Ticker ticker;

    private LocalDateTime dateTime;
    private BigDecimal previousClosePrice;
    private BigDecimal openPrice;
}
