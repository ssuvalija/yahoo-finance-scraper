package com.yahoo.finance.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "ticker")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ticker extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String tickerSymbol;
    private String companyName;
    private String marketCap;
    private int yearFounded;
    private String city;
    private String state;
    private String country;

    @JsonIgnore
    @OneToMany(mappedBy = "ticker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StockPrice> stockPrices;
    private int numberOfEmployees;
}
