# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Run
- Build the project: `./gradlew build`
- Run the application: `./gradlew bootRun`
- Run with specific profile: `./gradlew bootRun --args='--spring.profiles.active=dev'`
- Build JAR and run: `java -jar build/libs/stock-portfolio-backtest-api-*.jar`

### Testing
- Run all tests: `./gradlew test`
- Run tests with JUnit platform: `./gradlew test --info`

### Database
- H2 Console (dev profile): http://localhost:8080/h2-console
- Default H2 connection: `jdbc:h2:~/db/portfolio-backtest-api` with username `sa`

### API Documentation
- Swagger UI: http://localhost:8080/api-docs
- OpenAPI JSON: http://localhost:8080/api/v3

## Architecture Overview

This is a Spring Boot REST API for stock portfolio backtesting with the following key architectural patterns:

### Core Business Logic
- **Backtesting Engine**: `PortfolioBacktestService` orchestrates the main backtesting calculations
- **Portfolio Calculator**: `PortfolioCalculator` contains pure functions for financial calculations (compound returns, volatility, portfolio merging)
- **Data Interpolation**: Strategy pattern implementation in `strategy/` package for handling missing data points

### Authentication & Security
- JWT-based authentication with custom filter `JWTTokenValidatorFilter`
- BCrypt password encoding
- CORS configured for localhost:3000 (frontend development)
- Most endpoints require authentication except auth, public stock data, and API docs

### Data Layer
- JPA entities in `domain/` package with proper cascade relationships
- Repositories follow Spring Data JPA naming conventions
- Price data stored in separate calculation tables (`CalcStockPrice`, `CalcIndexPrice`)
- Redis integration for email verification caching

### Request/Response Pattern
- DTOs organized in `dto/request/` and `dto/response/` packages
- Custom validation annotations in `valid/` package
- Global exception handling in `GlobalExceptionHandler`

### Key Domain Models
- `Portfolio` -> `PortfolioItem` (one-to-many with cascade)
- `Stock` -> `StockPrice` and `CalcStockPrice` for historical data
- `Users` -> `Portfolio` (one-to-many relationship)

## Environment Configuration

### Required Environment Variables
```bash
# Database (Development)
DEV_DATASOURCE_URL=jdbc:h2:~/db/portfolio-backtest-api
DEV_DATASOURCE_USERNAME=sa
DEV_DATASOURCE_PASSWORD=

# Database (Production)  
PROD_DATASOURCE_URL=jdbc:mysql://localhost:3306/test1?createDatabaseIfNotExist=TRUE
PROD_DATASOURCE_USERNAME=root
PROD_DATASOURCE_PASSWORD=your-password

# Email Service
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password

# Profile
SPRING_PROFILES_ACTIVE=dev
```

### Profile-Specific Configurations
- `dev` profile: Uses H2 in-memory database, shows SQL logs
- `prod` profile: Uses MySQL, optimized for production
- Configuration files: `application-dev.properties`, `application-prod.properties`

## Important Financial Calculation Logic

### Backtesting Process
1. **Monthly Returns Calculation**: Individual stocks calculated first using `CalcStockPrice` data
2. **Portfolio Aggregation**: Stock returns weighted by portfolio allocation using `mergeStockIntoPortfolioRor`  
3. **Compound Returns**: Uses `calculateCompoundRor` for accurate multi-period performance
4. **Volatility**: Standard deviation of monthly returns via `calculateVolatility`
5. **Missing Data Handling**: Linear interpolation strategy for gaps in price data

### Key Classes for Financial Logic
- `PortfolioBacktestService`: Main orchestration and business logic
- `PortfolioCalculator`: Pure calculation functions (compound returns, volatility, portfolio merging)
- `DataInterpolationStrategy` / `LinearInterpolationStrategy`: Handles missing price data

### Performance Considerations
- Monthly calculations stored in `CalcStockPrice` and `CalcIndexPrice` tables for efficiency
- Async configuration available in `AsyncConfig` for background processing
- Redis caching for email verification to reduce database load