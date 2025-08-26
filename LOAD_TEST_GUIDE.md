# 🚀 포트폴리오 백테스팅 API - nGrinder 부하테스트 가이드

## 📋 목차
1. [사전 준비](#사전-준비)
2. [환경 설정](#환경-설정)  
3. [실행 방법](#실행-방법)
4. [테스트 시나리오](#테스트-시나리오)
5. [결과 분석](#결과-분석)
6. [문제 해결](#문제-해결)
7. [성능 최적화 가이드](#성능-최적화-가이드)

---

## 🔧 사전 준비

### 필수 소프트웨어
- **Docker & Docker Compose** 설치
- **Java 17** 설치  
- **Git** 설치
- **curl** 및 **jq** 설치 (성능 분석용)

### 사전 확인
```bash
# Docker 버전 확인
docker --version
docker-compose --version

# Java 버전 확인  
java -version

# 포트 사용 확인 (8080, 8081, 8082 포트가 비어있어야 함)
netstat -tuln | grep -E ':(8080|8081|8082)'
```

---

## ⚙️ 환경 설정

### 1단계: 프로젝트 빌드
```bash
cd /home/ind/code/st-pro/stock-portfolio-backtest-api

# 애플리케이션 빌드 (테스트 제외)
./gradlew build -x test
```

### 2단계: Docker 이미지 빌드용 Dockerfile 생성
```bash
# Dockerfile이 없다면 생성
cat > Dockerfile << 'EOF'
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
EOF
```

---

## 🚀 실행 방법

### 방법 1: 원클릭 실행 (권장)
```bash
# 모든 환경을 자동으로 설정하고 시작
./run-load-test.sh
```

### 방법 2: 수동 단계별 실행

#### Step 1: 애플리케이션 빌드
```bash
./gradlew clean build -x test
```

#### Step 2: nGrinder 환경 시작
```bash
docker-compose -f docker-compose.ngrinder.yml up -d
```

#### Step 3: 서비스 상태 확인
```bash
# 컨테이너 상태 확인
docker ps

# API 서버 Health Check
curl http://localhost:8081/actuator/health

# nGrinder Controller 접속 확인
curl http://localhost:8080
```

#### Step 4: nGrinder Web UI 접속
1. 브라우저에서 `http://localhost:8080` 접속
2. 기본 계정으로 로그인: `admin` / `admin`

---

## 🎯 테스트 시나리오

### 시나리오 1: 종합 부하테스트
**파일**: `ngrinder-scripts/PortfolioBacktestLoadTest.groovy`

**테스트 플로우**:
1. 사용자 로그인
2. 포트폴리오 백테스팅 수행
3. 백테스팅 결과로 포트폴리오 저장
4. 저장된 포트폴리오 조회

**설정 예시**:
- **Virtual Users**: 50명
- **Duration**: 5분
- **Ramp-up**: 10초 간격으로 증가

### 시나리오 2: 백테스팅 스트레스 테스트
**파일**: `ngrinder-scripts/BacktestStressTest.groovy`

**테스트 구성**:
- 60% 가벼운 백테스팅 (3종목, 1년)
- 30% 무거운 백테스팅 (10종목, 5년)
- 10% 극한 백테스팅 (20종목, 10년)

**설정 예시**:
- **Virtual Users**: 100명
- **Duration**: 10분
- **CPU 집약적 워크로드**

---

## 📊 nGrinder Web UI 사용법

### 1. 스크립트 업로드
1. **Script** 메뉴 클릭
2. **Upload** 버튼 클릭  
3. Groovy 스크립트 파일 업로드:
   - `PortfolioBacktestLoadTest.groovy`
   - `BacktestStressTest.groovy`

### 2. 테스트 생성 및 실행

#### 기본 설정
```
Test Name: Portfolio API Load Test
Script: PortfolioBacktestLoadTest.groovy
Target Host: portfolio-api:8080 (Docker 네트워크 내부)
```

#### 부하 설정
```
Virtual Users: 50
Duration: 5m (5분)
Ramp-Up: Enable
Process: 2
Thread per Process: 25
```

#### 고급 설정
```
Think Time: 1000ms (사용자 대기시간)
Enable Statistics: Yes
```

### 3. 에이전트 관리
1. **Agent Management** 메뉴 확인
2. 에이전트 상태가 **Ready**인지 확인
3. 필요시 에이전트 추가/제거

---

## 📈 결과 분석

### 실시간 모니터링
```bash
# 성능 분석 스크립트 실행
./analyze-performance.sh
```

### 주요 지표

#### 1. TPS (Transactions Per Second)
- **목표**: 100 TPS 이상
- **확인 방법**: nGrinder 대시보드 → TPS 그래프

#### 2. 응답시간
- **평균 응답시간**: < 2초
- **95th Percentile**: < 5초
- **최대 응답시간**: < 10초

#### 3. 에러율
- **목표**: < 1%
- **주요 에러**: 타임아웃, 메모리 부족, 커넥션 풀 고갈

#### 4. 시스템 리소스
```bash
# CPU 사용률 확인
curl -s http://localhost:8082/actuator/metrics/system.cpu.usage

# 메모리 사용률 확인  
curl -s http://localhost:8082/actuator/metrics/jvm.memory.used

# 데이터베이스 커넥션 확인
curl -s http://localhost:8082/actuator/metrics/hikaricp.connections.active
```

---

## 🛠️ 문제 해결

### 자주 발생하는 문제들

#### 1. 포트 충돌
**증상**: `Port already in use` 에러
**해결책**:
```bash
# 포트 사용 프로세스 확인
sudo netstat -tuln | grep :8080

# 프로세스 종료 후 재시작
docker-compose -f docker-compose.ngrinder.yml down
docker-compose -f docker-compose.ngrinder.yml up -d
```

#### 2. API 서버 시작 실패
**증상**: Health check 실패
**해결책**:
```bash
# 로그 확인
docker logs portfolio-api-test

# 데이터베이스 연결 확인
docker exec -it portfolio-api-test curl localhost:8080/actuator/health
```

#### 3. 메모리 부족
**증상**: OutOfMemoryError, GC 과부하
**해결책**:
```bash
# JVM 힙 크기 증가 (docker-compose.yml 수정)
environment:
  - JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC
```

#### 4. 데이터베이스 커넥션 부족
**증상**: Connection timeout 에러
**해결책**:
```properties
# application-dev.properties에 추가
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

---

## 🚀 성능 최적화 가이드

### 즉시 적용 가능한 최적화

#### 1. JVM 튜닝
```bash
# docker-compose.yml의 environment 섹션에 추가
JAVA_OPTS: >
  -Xms1g -Xmx2g
  -XX:+UseG1GC
  -XX:MaxGCPauseMillis=200
  -XX:+HeapDumpOnOutOfMemoryError
```

#### 2. 데이터베이스 최적화
```properties
# application-dev.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
```

#### 3. 캐싱 도입 (Redis)
```java
@Cacheable(value = "backtest-cache", key = "#request.hashCode()")
public PortfolioBacktestResponseDTO calculatePortfolio(PortfolioBacktestRequestDTO request) {
    // 기존 계산 로직
}
```

#### 4. 비동기 처리
```java
@Async("backtestExecutor")
@Service
public class AsyncBacktestService {
    public CompletableFuture<BacktestResult> calculateAsync(Portfolio portfolio) {
        // 비동기 백테스팅
    }
}
```

### 성능 벤치마크 목표

| 지표 | 기본 | 목표 | 최적화 후 |
|------|------|------|-----------|
| TPS | 50 | 100 | 200+ |
| 평균 응답시간 | 3초 | 2초 | 1초 |
| 95th Percentile | 8초 | 5초 | 3초 |
| 에러율 | 5% | 1% | 0.1% |
| 메모리 사용량 | 1GB | 800MB | 600MB |

---

## 🔄 테스트 종료 및 정리

### 환경 정리
```bash
# nGrinder 환경 종료
docker-compose -f docker-compose.ngrinder.yml down

# 볼륨 제거 (데이터 초기화)
docker-compose -f docker-compose.ngrinder.yml down -v

# 사용하지 않는 이미지 정리
docker system prune -f
```

### 테스트 보고서 생성
```bash
# 테스트 결과를 CSV로 내보내기 (nGrinder UI에서)
# Performance Test → Detail Report → Download CSV

# 성능 분석 결과 저장
./analyze-performance.sh > performance-report-$(date +%Y%m%d).txt
```

---

## 📞 추가 지원

### 더 상세한 모니터링이 필요하다면:
1. **Prometheus + Grafana** 대시보드 구성
2. **APM 도구** (Pinpoint, New Relic) 연동  
3. **로그 분석** (ELK Stack) 구축

### 성능 이슈 발생 시:
1. `analyze-performance.sh` 실행하여 현재 상태 파악
2. nGrinder 테스트 결과와 비교 분석
3. 병목 구간 식별 후 단계적 최적화 적용

---

**✨ 이제 포트폴리오 백테스팅 API의 성능 한계를 정확히 파악할 수 있습니다!**