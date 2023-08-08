package com.yahoo.finace.scraper.repository;

import com.yahoo.finace.scraper.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
}
