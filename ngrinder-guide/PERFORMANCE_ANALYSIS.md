# 📊 포트폴리오 백테스팅 API 성능 분석 가이드

## 🎯 성능 분석 개요

이 문서는 nGrinder 부하테스트 결과를 분석하고 성능 병목점을 식별하여 최적화 방향을 제시합니다.

---

## 📈 핵심 성능 지표 (KPI)

### 1. 처리량 지표
- **TPS (Transactions Per Second)**: 초당 처리 트랜잭션 수
- **처리량 목표**: 100 TPS 이상
- **측정 방법**: nGrinder 대시보드 → TPS 그래프

### 2. 응답시간 지표
- **평균 응답시간**: 모든 요청의 평균 처리 시간
- **95th Percentile**: 95%의 요청이 이 시간 이내 처리
- **최대 응답시간**: 가장 오래 걸린 요청의 시간

```
목표값:
- 평균 응답시간: < 2초
- 95th Percentile: < 5초  
- 최대 응답시간: < 10초
```

### 3. 안정성 지표
- **에러율**: 전체 요청 중 실패한 요청의 비율
- **목표**: 1% 미만
- **허용 한계**: 5% 미만

---

## 🔍 자동 성능 분석 도구

### 실행 방법
```bash
# 부하테스트 실행 중 또는 완료 후
./analyze-performance.sh
```

### 분석 결과 해석

#### JVM 메모리 분석
```bash
# 힙 메모리 사용률 확인
curl -s http://localhost:8082/actuator/metrics/jvm.memory.used?tag=area:heap

# 해석 기준:
# 70% 이하: 양호
# 70-85%: 주의 (모니터링 필요)
# 85% 이상: 위험 (힙 크기 증가 필요)
```

#### CPU 사용률 분석
```bash
# CPU 사용률 확인
curl -s http://localhost:8082/actuator/metrics/system.cpu.usage

# 해석 기준:
# 60% 이하: 여유
# 60-80%: 적정
# 80% 이상: 병목 (최적화 필요)
```

#### 데이터베이스 커넥션 풀 분석
```bash
# 활성 커넥션 확인
curl -s http://localhost:8082/actuator/metrics/hikaricp.connections.active

# 해석:
# 활성 커넥션 / 최대 커넥션 비율이 80% 이상이면 풀 크기 증가 필요
```

---

## 📊 nGrinder 테스트 결과 분석

### 테스트 결과 화면 구성

#### 1. TPS 그래프
- **X축**: 시간 (테스트 진행 시간)
- **Y축**: TPS (초당 트랜잭션)
- **분석 포인트**:
  - 초기 Ramp-up 구간의 안정성
  - 정상 상태에서의 TPS 유지
  - 테스트 종료 시 점진적 감소

#### 2. 응답시간 분포
- **평균 응답시간 추이**
- **최소/최대 응답시간**  
- **95th, 99th Percentile**

#### 3. 에러 통계
- **총 에러 수**
- **에러 타입별 분류**
- **시간대별 에러 발생 패턴**

### 성능 패턴 분석

#### 정상적인 패턴
```
TPS: 일정하게 유지 (목표치 근처)
응답시간: 낮고 안정적
에러율: 1% 미만
메모리: 점진적 증가 후 GC로 감소 반복
```

#### 문제가 있는 패턴
```
TPS: 시간이 지남에 따라 감소
응답시간: 점진적 증가 또는 급격한 스파이크
에러율: 5% 이상 또는 지속적 증가
메모리: 지속적 증가 (메모리 누수)
```

---

## 🔬 상세 성능 진단

### 1. 백테스팅 API 성능 분석

#### 백테스팅 복잡도별 성능
```groovy
// 테스트 시나리오별 응답시간 목표
가벼운 백테스팅 (3종목, 1년): < 1초
일반 백테스팅 (10종목, 5년): < 3초
무거운 백테스팅 (20종목, 10년): < 10초
```

#### 병목점 식별
```sql
-- 데이터베이스 쿼리 분석
-- 가장 오래 걸리는 쿼리 확인
SELECT * FROM CalcStockPrice 
WHERE stock_id IN (1,2,3,4,5,6,7,8,9,10) 
AND base_date BETWEEN '2019-01-01' AND '2023-12-31';
```

### 2. 메모리 사용 패턴 분석

#### 정상적인 메모리 패턴
```
1. 요청 처리 시 메모리 증가
2. 응답 완료 후 일시적 메모리 유지
3. GC 실행으로 메모리 해제
4. 1-3 반복
```

#### 메모리 누수 패턴
```
1. 요청 처리 시 메모리 증가
2. 응답 완료 후에도 메모리 유지
3. GC 실행해도 메모리 감소 없음
4. 지속적 메모리 증가 → OutOfMemoryError
```

### 3. 스레드 및 커넥션 분석

#### 스레드 풀 상태 확인
```bash
# Tomcat 스레드 확인
curl -s http://localhost:8082/actuator/metrics/tomcat.threads.busy
curl -s http://localhost:8082/actuator/metrics/tomcat.threads.config.max
```

#### 커넥션 풀 효율성 분석
```bash
# 커넥션 획득 대기 시간
curl -s http://localhost:8082/actuator/metrics/hikaricp.connections.acquire

# 평균 대기 시간이 100ms 이상이면 풀 크기 부족
```

---

## 🚀 성능 최적화 전략

### 단계별 최적화 접근

#### Phase 1: 즉시 적용 (Low-hanging Fruits)
```properties
# application-dev.properties 최적화
# 커넥션 풀 크기 증가
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# JVM 설정 최적화
JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

#### Phase 2: 알고리즘 최적화
```java
// 백테스팅 계산 최적화
@Service
public class OptimizedBacktestService {
    
    // 배치 처리로 N+1 문제 해결 (이미 구현됨)
    private Map<Stock, Map<LocalDate, Float>> calculateAllStockMonthlyRor(
            List<Stock> stocks, LocalDate startDate, LocalDate endDate) {
        // 기존 최적화된 배치 쿼리 사용
    }
    
    // 병렬 처리 도입
    @Async
    public CompletableFuture<Map<LocalDate, Float>> calculateStockRorAsync(Stock stock) {
        return CompletableFuture.completedFuture(calculateStockMonthlyRor(stock));
    }
}
```

#### Phase 3: 캐싱 시스템 도입
```java
@Component
@CacheConfig(cacheNames = "backtest-cache")
public class CachedBacktestService {
    
    @Cacheable(key = "#request.hashCode()")
    public PortfolioBacktestResponseDTO calculatePortfolio(
            PortfolioBacktestRequestDTO request) {
        // TTL: 1시간, 동일 요청에 대해 캐시된 결과 반환
    }
}
```

#### Phase 4: 아키텍처 개선
```yaml
# 마이크로서비스 분리
services:
  portfolio-service:     # 포트폴리오 CRUD
    cpu: 0.5
    memory: 512MB
    
  backtest-service:      # 백테스팅 계산 (CPU 집약적)
    cpu: 2.0
    memory: 2GB
    
  cache-service:         # Redis 캐시
    memory: 1GB
```

---

## 📋 성능 벤치마크 및 목표 설정

### 현재 성능 vs 목표 성능

| 지표 | 현재 | 목표 | 최적화 후 목표 |
|------|------|------|----------------|
| **TPS** | 50 | 100 | 200+ |
| **평균 응답시간** | 3초 | 2초 | 1초 |
| **95th Percentile** | 8초 | 5초 | 3초 |
| **에러율** | 5% | 1% | 0.1% |
| **메모리 사용량** | 1.5GB | 1GB | 800MB |
| **CPU 사용률** | 85% | 70% | 60% |

### 테스트 시나리오별 목표

#### 가벼운 워크로드 (일반 사용자)
```
Virtual Users: 50
Portfolio Size: 3-5 종목
Period: 1-3년
목표 TPS: 150+
목표 응답시간: < 1초
```

#### 무거운 워크로드 (전문 사용자)  
```
Virtual Users: 20
Portfolio Size: 10-15 종목  
Period: 5-10년
목표 TPS: 30+
목표 응답시간: < 5초
```

#### 극한 워크로드 (스트레스 테스트)
```
Virtual Users: 100
Portfolio Size: 20+ 종목
Period: 10+ 년  
목표: 시스템 다운 없이 처리
목표 에러율: < 5%
```

---

## 🔧 실시간 모니터링 설정

### Grafana 대시보드 구성 (권장)
```yaml
# docker-compose.monitoring.yml
version: '3.8'
services:
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

### 모니터링할 핵심 메트릭스
```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'portfolio-api'
    static_configs:
      - targets: ['localhost:8082']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
```

### 알럿 규칙 설정
```yaml
# alert-rules.yml
groups:
  - name: portfolio-api
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_total{status=~"5.."}[5m]) > 0.05
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "높은 에러율 감지"
          
      - alert: HighResponseTime  
        expr: histogram_quantile(0.95, rate(http_server_requests_duration_seconds_bucket[5m])) > 5
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "응답시간 임계값 초과"
```

---

## 📊 성능 리포트 템플릿

### 부하테스트 결과 요약
```
=== 포트폴리오 백테스팅 API 부하테스트 결과 ===

📅 테스트 일시: 2024-01-01 10:00-10:10
⏱️ 테스트 시간: 10분
👥 동시 사용자: 50명
📊 총 요청 수: 30,000건

🎯 핵심 지표
├── TPS: 평균 85.2, 최대 120.5
├── 응답시간: 평균 2.1초, 95th 4.8초
├── 에러율: 0.8% (240건)
└── 처리량: 총 29,760건 성공

💻 시스템 리소스
├── CPU: 평균 72%, 최대 89%
├── 메모리: 평균 1.2GB, 최대 1.8GB
├── 힙 사용률: 평균 65%, 최대 82%
└── DB 커넥션: 평균 12개, 최대 18개

✅ 성공 기준 달성 여부
├── TPS 목표 (100): ❌ (85.2)
├── 응답시간 (< 2초): ❌ (2.1초)
├── 에러율 (< 1%): ✅ (0.8%)
└── 안정성: ✅ (다운 없음)

🎯 개선 권장사항
1. JVM 힙 크기 2GB로 증가
2. 백테스팅 계산 병렬 처리 도입
3. Redis 캐싱 시스템 적용
4. 데이터베이스 인덱스 추가 최적화
```

### 지속적 성능 개선 프로세스
```
1. 주간 성능 테스트 실행
2. 성능 지표 트렌드 분석
3. 병목점 식별 및 우선순위 설정
4. 단계별 최적화 적용
5. 결과 측정 및 검증
6. 1단계로 돌아가 반복
```

---

**🎯 성능 분석은 지속적인 프로세스입니다. 정기적인 모니터링과 최적화를 통해 사용자에게 최고의 경험을 제공하세요!**