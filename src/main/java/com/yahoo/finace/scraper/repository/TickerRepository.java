package com.yahoo.finace.scraper.repository;

import com.yahoo.finace.scraper.model.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TickerRepository extends JpaRepository<Ticker, Long> {
}
