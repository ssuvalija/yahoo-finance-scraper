# yahoo-finance-scraper

## About Project

yahoo-finance-scraper is a Java-based backend application that provides APIs for fetching and managing stock market data. It leverages Spring Boot to implement the backend functionality and communicate with external data sources.

Key Features:

1. **Data Retrieval and Storage**: The app employs scraping techniques to gather data from Yahoo Finance's website.

2. **Historical Data Storage**: In addition to the gathered data, the application stores historical data for the past 5 years. 

3. **Regular Data Updates**: The stored data is refreshed at regular intervals, which can be configured based on your preferences. By default, the data is updated every 15 minutes to provide users with the latest insights.

4. **Scheduled Data Maintenance**: To maintain data accuracy and optimize storage, the application includes a scheduled task that automatically clears older data points (those more than 5 years old).

## Project Structure

The project follows a well-organized directory structure to manage different aspects of the application:

- `Controller`: Contains the main logic for handling API requests and responses.
- `DTO`: Data Transfer Objects representing the data structures exchanged with the API.
- `Mapper`: Handles the mapping between DTOs and domain models.
- `Model`: Domain models representing the core entities of the application.
- `Repository`: Manages data retrieval and storage.
- `Service`: Implements business logic and interactions with the API and repositories.
- `Validator`: Validates input data and ensures its integrity.

## Getting Started

1. Clone the repository:

   ```sh
   git clone git@github.com:ssuvalija/yahoo-finance-scraper.git
   cd yahoo-finance-scraper

2. yahoo-finance-scraper relies on MySQL 8.0.32 for data storage. You can conveniently set up MySQL using Docker by running the following command in your terminal:
   ```sh
   docker run -d -p 3306:3306 --name mysql-8.0.32 -e MYSQL_ROOT_PASSWORD=your-root-password mysql:8.0.32
   ```
   
   Replace **your-root-password** with a secure password of your choice. This command will pull the MySQL 8.0.32 image and start a container with the necessary configuration.

   After the Docker container is up and running, access the MySQL instance using your preferred client (e.g., MySQL Workbench or CLI). Create a new database named **yahoo_finance**.

   To allow the backend application to interact with the MySQL database, you need to configure the database credentials in the application.properties file of the application.

   Open the **application.properties** file and update the following properties with your MySQL database credentials:

   ```sh
   spring.datasource.url=jdbc:mysql://localhost:3306/yahoo_finance
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   ```

   Replace **your-username** and **your-password** with the appropriate MySQL user credentials.

## Examples: How to Call API

1. **Fetching Trending Tickers**

   To retrieve a list of trending tickers from the API:

   ```swift
   APIManager.shared.sendRequest(path: "/trending-tickers", method: "GET") { (result: Result<TrendingTickersResponse, Error>) in
       switch result {
       case .success(let response):
           // Handle the list of trending tickers in response.data
       case .failure(let error):
           print("Error fetching trending tickers: \(error)")
       }
   }
