# 📈 주식 포트폴리오 백테스팅 API

Spring Boot 기반의 RESTful API로, 사용자가 주식 포트폴리오를 구성하고 과거 데이터를 바탕으로 백테스팅할 수 있는 서비스를 제공합니다.

---

## 🗂️ 목차

1. [프로젝트 개요](#프로젝트-개요)
2. [주요 기능](#주요-기능)
3. [기술 스택](#기술-스택)
4. [실행 방법](#실행-방법)
5. [프로젝트 구조](#프로젝트-구조)
6. [ERD](#erd)

---

## 📌 프로젝트 개요

이 프로젝트는 사용자가 직접 포트폴리오를 만들고, 과거 주가 데이터를 활용해 성과를 시뮬레이션할 수 있는 백엔드 API입니다.

---

## 🚀 주요 기능

- **사용자 인증**
  - JWT 기반 회원가입 및 로그인
- **포트폴리오 관리**
  - 포트폴리오 생성, 조회, 수정, 삭제 (CRUD)
  - 포트폴리오 내 종목 및 비중 관리
- **주식 및 지수 정보**
  - 종목 정보 및 주가 데이터(일별 등) 조회
  - 주요 지수 정보 및 가격 데이터 제공
- **백테스팅**
  - 포트폴리오 과거 성과 시뮬레이션
  - 기간별 수익률, 변동성 등 지표 계산
  - 주요 지수 대비 성과 비교 (Index Backtesting)
- **API 문서**
  - Swagger UI 제공

---

## 🛠️ 기술 스택

- **언어**: Java
- **프레임워크**: Spring Boot (Web, Security, Data JPA)
- **빌드 도구**: Gradle
- **인증**: JWT (JSON Web Tokens)
- **API 문서화**: Swagger / OpenAPI
- **데이터베이스**: H2, MySQL 등 관계형 DB

---

## ⚡ 실행 방법

1. **저장소 클론**
   ```bash
   git clone <repository-url>
   cd stock-portfolio-backtest-api
   ```
2. **DB 설정**
   - `src/main/resources/application.properties`에서 데이터베이스 연결 정보 수정
3. **애플리케이션 빌드**
   ```bash
   ./gradlew build
   ```
4. **애플리케이션 실행**
   ```bash
   java -jar build/libs/stock-portfolio-backtest-api-*.jar
   ```
   또는 IDE에서 `StockPortfolioBacktestApiApplication.java` 실행
5. **API 문서 접속**
   - [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🏗️ 프로젝트 구조

```
.
├── build.gradle
├── gradlew
├── gradlew.bat
├── settings.gradle
├── src
│   ├── main
│   │   ├── java/com/chan/stock_portfolio_backtest_api
│   │   │   ├── StockPortfolioBacktestApiApplication.java  # 메인 클래스
│   │   │   ├── config          # 설정 (Security, Swagger 등)
│   │   │   ├── constants       # 상수 정의
│   │   │   ├── controller      # API 엔드포인트
│   │   │   ├── data            # 데이터 관련 클래스
│   │   │   ├── domain          # JPA 엔티티
│   │   │   ├── dto             # DTO
│   │   │   ├── exception       # 예외 처리
│   │   │   ├── filter          # 서블릿 필터
│   │   │   ├── repository      # JPA Repository
│   │   │   ├── service         # 비즈니스 로직
│   │   │   ├── util            # 유틸리티
│   │   │   └── valid           # 유효성 검증
│   │   └── resources
│   │       ├── application.properties
│   │       └── data.sql
│   └── test
│       └── java/com/chan/stock_portfolio_backtest_api
└── README.md
```

---

## 📊 ERD

아래는 프로젝트의 ERD(Entity-Relationship Diagram)입니다. 주요 엔티티와 관계를 시각적으로 표현했습니다.

```
+----------------+       +----------------+       +----------------+
|     User       |       |   Portfolio    |       |     Stock      |
+----------------+       +----------------+       +----------------+
| id             |       | id             |       | id             |
| username       |       | name           |       | symbol         |
| password       |       | user_id        |       | name           |
| email          |       +----------------+       | price          |
+----------------+               |                +----------------+
        |                        |                        |
        |                        |                        |
        v                        v                        v
+----------------+       +----------------+       +----------------+
|  PortfolioItem |       |  Backtest      |       |  StockPrice    |
+----------------+       +----------------+       +----------------+
| id             |       | id             |       | id             |
| portfolio_id   |       | portfolio_id   |       | stock_id       |
| stock_id       |       | start_date     |       | date           |
| weight         |       | end_date       |       | price          |
+----------------+       | result         |       +----------------+
                         +----------------+
```

---

더 궁금한 점이나 추가 설명이 필요하면 언제든 말씀해 주세요!
