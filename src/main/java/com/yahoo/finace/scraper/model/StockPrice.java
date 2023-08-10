package com.yahoo.finace.scraper.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "stock_price",
        uniqueConstraints=@UniqueConstraint(columnNames={"tickerId", "date"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockPrice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tickerId")
    private Ticker ticker;

    private LocalDate date;
    private BigDecimal previousClosePrice;
    private BigDecimal openPrice;
    private boolean isMarketOpen;
}
