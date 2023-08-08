package com.yahoo.finace.scraper;

import com.yahoo.finace.scraper.utils.YahooScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories("com.yahoo.finace.scraper.repository")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class Application {

	@Autowired
	private static YahooScraper yahooScraper;
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		List<String> tickerSymbols = new ArrayList<>();
		tickerSymbols.add("BA");
		tickerSymbols.add("BTC-USD");

		try {
			yahooScraper.fetchData(tickerSymbols);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
