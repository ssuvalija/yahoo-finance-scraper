package com.yahoo.finace.scraper.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticker")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ticker {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tickerId;
    private String tickerSymbol;
    private String companyName;
    private String marketCap;
    private int yearFounded;
    //TODO: investigate what is the best practice for limiting number of chars
    private String city;
    private String state;
}
