# 주식 포트폴리오 백테스팅 API (Stock Portfolio Backtesting API)

## 프로젝트 개요

이 프로젝트는 사용자가 주식 포트폴리오를 구성하고, 과거 데이터를 기반으로 해당 포트폴리오의 성과를 백테스팅할 수 있는 기능을 제공하는 RESTful API입니다. Spring Boot 프레임워크를 기반으로
개발되었습니다.

## 주요 기능

* **사용자 인증:** JWT 토큰 기반의 회원가입 및 로그인 기능을 제공합니다.
* **포트폴리오 관리:**
    * 사용자별 포트폴리오 생성, 조회, 수정, 삭제 (CRUD)
    * 포트폴리오 내 주식 종목 및 비중 관리
* **주식 및 지수 정보:**
    * 주식 종목 정보 조회
    * 주가 데이터 조회 (일별 등)
    * 주요 지수 정보 및 가격 데이터 조회
* **백테스팅:**
    * 사용자가 구성한 포트폴리오의 과거 성과 시뮬레이션
    * 특정 기간 동안의 수익률, 변동성 등 지표 계산
    * 주요 지수 대비 성과 비교 (Index Backtesting)
* **API 문서:** Swagger UI를 통해 API 명세서를 제공하여 개발 편의성을 높입니다.

## 기술 스택

* **언어:** Java
* **프레임워크:** Spring Boot (Web, Security, Data JPA)
* **빌드 도구:** Gradle
* **인증:** JSON Web Tokens (JWT)
* **API 문서화:** Swagger / OpenAPI
* **데이터베이스:** 관계형 데이터베이스 (H2, MySQL)

## 실행 방법

1. **저장소 복제:**
   ```bash
   git clone <repository-url>
   cd stock-portfolio-backtest-api
   ```
2. **데이터베이스 설정:** `src/main/resources/application.properties` 파일에서 데이터베이스 연결 정보를 설정합니다. (필요 시)
3. **애플리케이션 빌드:**
   ```bash
   ./gradlew build
   ```
4. **애플리케이션 실행:**
   ```bash
   java -jar build/libs/stock-portfolio-backtest-api-*.jar
   ```
   또는 IDE를 통해 `src/main/java/com/chan/stock_portfolio_backtest_api/StockPortfolioBacktestApiApplication.java` 파일을 실행합니다.
5. **API 접근:** 애플리케이션 실행 후, 웹 브라우저나 API 클라이언트 도구를 사용하여 API에 접근합니다.
    * Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## 프로젝트 구조

```
.
├── build.gradle              # Gradle 빌드 스크립트
├── gradlew                   # Gradle Wrapper (Linux/Mac)
├── gradlew.bat               # Gradle Wrapper (Windows)
├── settings.gradle           # Gradle 설정
├── src
│   ├── main
│   │   ├── java/com/chan/stock_portfolio_backtest_api
│   │   │   ├── StockPortfolioBacktestApiApplication.java # Spring Boot 메인 애플리케이션 클래스
│   │   │   ├── config          # 설정 클래스 (Security, Swagger, Async 등)
│   │   │   ├── constants       # 상수 정의 (Role, AppConstants 등)
│   │   │   ├── controller      # API 엔드포인트 정의 (Controller)
│   │   │   ├── data            # 데이터 관련 클래스 (Queue, Token 등)
│   │   │   ├── domain          # 데이터베이스 엔티티 (JPA Entity)
│   │   │   ├── dto             # 데이터 전송 객체 (Request/Response DTO)
│   │   │   ├── exception       # 예외 처리 관련 클래스
│   │   │   ├── filter          # 서블릿 필터 (JWT 검증 등)
│   │   │   ├── repository      # 데이터 접근 계층 (JPA Repository)
│   │   │   ├── service         # 비즈니스 로직 구현 (Service)
│   │   │   ├── util            # 유틸리티 클래스 (JWT 생성/검증 등)
│   │   │   └── valid           # 유효성 검증 관련 클래스
│   │   └── resources
│   │       ├── application.properties # 애플리케이션 설정 파일
│   │       └── data.sql        # 초기 데이터 스크립트 (선택 사항)
│   └── test
│       └── java/com/chan/stock_portfolio_backtest_api # 단위/통합 테스트 코드
└── README.md                 # 프로젝트 설명 파일 (현재 파일)
