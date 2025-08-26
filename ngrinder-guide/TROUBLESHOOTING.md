# 🛠️ nGrinder 부하테스트 문제 해결 가이드

## 🚨 자주 발생하는 문제들

### 1. 포트 충돌 문제
**증상**: `Port 8080 is already in use` 에러

**해결 방법**:
```bash
# 방법 1: 포트 사용 프로세스 확인 및 종료
sudo netstat -tuln | grep -E ':(8080|8081|8082)'
sudo pkill -f "8080"

# 방법 2: 모든 관련 프로세스 종료
sudo pkill -f "ngrinder\|portfolio-api"

# 방법 3: Docker 컨테이너 전체 정리
docker-compose -f docker-compose.ngrinder.yml down
docker system prune -f
```

### 2. Docker 관련 문제
**증상**: `docker-compose` 명령어 실행 실패

**해결 방법**:
```bash
# Docker 데몬 상태 확인
sudo systemctl status docker

# Docker 데몬 시작
sudo systemctl start docker

# 사용자 Docker 그룹 추가
sudo usermod -aG docker $USER
newgrp docker

# Docker Compose 버전 확인
docker-compose --version
```

### 3. 애플리케이션 빌드 실패
**증상**: `./gradlew build` 실패

**해결 방법**:
```bash
# Java 버전 확인
java -version
# Java 17이 아니면 설치 필요

# Gradle 래퍼 권한 부여
chmod +x ./gradlew

# 깨끗한 빌드
./gradlew clean build -x test

# 의존성 문제 시
./gradlew --refresh-dependencies clean build -x test
```

### 4. API 서버 Health Check 실패
**증상**: `curl: (7) Failed to connect to localhost:8081`

**진단 방법**:
```bash
# 컨테이너 상태 확인
docker ps

# API 서버 로그 확인
docker logs portfolio-api-test

# 네트워크 연결 테스트
docker exec -it portfolio-api-test curl localhost:8080/actuator/health
```

**해결 방법**:
```bash
# 컨테이너 재시작
docker-compose -f docker-compose.ngrinder.yml restart portfolio-api

# 포트 매핑 확인
docker port portfolio-api-test

# 방화벽 확인 (Linux)
sudo ufw status
```

### 5. 메모리 부족 문제
**증상**: `OutOfMemoryError`, 컨테이너 재시작 반복

**해결 방법**:
```bash
# docker-compose.ngrinder.yml 수정
services:
  portfolio-api:
    environment:
      - JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC

# 시스템 메모리 확인
free -h

# Docker 메모리 제한 확인
docker stats
```

### 6. nGrinder Agent 연결 실패
**증상**: Agent 상태가 `Inactive` 또는 `Disconnected`

**해결 방법**:
```bash
# Agent 컨테이너 로그 확인
docker logs ngrinder-agent

# Controller와 Agent 네트워크 확인
docker network ls
docker network inspect stock-portfolio-backtest-api_ngrinder-network

# Agent 재시작
docker-compose -f docker-compose.ngrinder.yml restart ngrinder-agent
```

### 7. 데이터베이스 연결 문제
**증상**: `Connection refused` 또는 `Database not found`

**해결 방법**:
```bash
# H2 데이터베이스 콘솔 접속
# URL: http://localhost:8081/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: (비워둠)

# 데이터베이스 초기화
docker-compose -f docker-compose.ngrinder.yml down -v
docker-compose -f docker-compose.ngrinder.yml up -d
```

---

## ⚡ 빠른 문제 해결 체크리스트

### 🔍 1단계: 기본 확인
- [ ] Docker가 실행 중인가?
- [ ] 필요한 포트(8080, 8081, 8082)가 비어있는가?
- [ ] Java 17이 설치되어 있는가?
- [ ] 충분한 디스크 공간이 있는가? (`df -h`)

### 🔧 2단계: 서비스 상태 확인
```bash
# 모든 컨테이너 상태
docker ps -a

# 특정 서비스 로그
docker logs ngrinder-controller
docker logs ngrinder-agent  
docker logs portfolio-api-test

# 네트워크 연결
curl http://localhost:8080  # nGrinder
curl http://localhost:8081/actuator/health  # API
```

### 🚨 3단계: 강제 복구
```bash
# 전체 환경 초기화
docker-compose -f docker-compose.ngrinder.yml down -v
docker system prune -f
./run-load-test.sh
```

---

## 🐛 테스트 실행 중 발생하는 문제들

### 1. 높은 에러율
**증상**: nGrinder 테스트에서 에러율 > 5%

**원인 분석**:
```bash
# API 서버 상태 확인
curl http://localhost:8081/actuator/health

# 메모리 사용량 확인
curl http://localhost:8082/actuator/metrics/jvm.memory.used

# CPU 사용률 확인
curl http://localhost:8082/actuator/metrics/system.cpu.usage
```

**해결 방법**:
- 동시 사용자 수 줄이기 (50 → 20)
- 테스트 지속 시간 단축 (10분 → 5분)  
- JVM 힙 메모리 증가
- 데이터베이스 커넥션 풀 크기 증가

### 2. 느린 응답 시간
**증상**: 평균 응답시간 > 5초

**최적화 방법**:
```bash
# 캐싱 활성화 (Redis)
# 데이터베이스 인덱스 추가
# 비동기 처리 적용
# 쿼리 최적화
```

### 3. 테스트 도중 서버 다운
**증상**: API 서버가 응답하지 않음

**응급 조치**:
```bash
# 서버 재시작
docker-compose -f docker-compose.ngrinder.yml restart portfolio-api

# 메모리 상태 확인
docker stats portfolio-api-test

# JVM 힙덤프 분석 (필요시)
docker exec portfolio-api-test jmap -dump:format=b,file=/tmp/heapdump.hprof 1
```

---

## 📊 성능 모니터링 및 디버깅

### 실시간 모니터링 명령어
```bash
# 1초마다 컨테이너 리소스 확인
watch docker stats

# API 메트릭스 모니터링
watch curl -s http://localhost:8082/actuator/metrics/http.server.requests

# 데이터베이스 커넥션 모니터링
watch curl -s http://localhost:8082/actuator/metrics/hikaricp.connections.active
```

### 로그 분석 도구
```bash
# 실시간 로그 추적
docker logs -f portfolio-api-test

# 에러 로그만 필터링
docker logs portfolio-api-test 2>&1 | grep -i error

# 특정 시간대 로그 확인
docker logs portfolio-api-test --since="2024-01-01T10:00:00" --until="2024-01-01T11:00:00"
```

---

## 🆘 긴급 복구 절차

### 전체 시스템 리셋
```bash
#!/bin/bash
echo "🚨 긴급 복구 시작..."

# 1. 모든 관련 프로세스 종료
docker-compose -f docker-compose.ngrinder.yml down -v
sudo pkill -f "ngrinder\|portfolio"

# 2. Docker 정리
docker system prune -af
docker volume prune -f

# 3. 네트워크 정리
docker network prune -f

# 4. 애플리케이션 재빌드
./gradlew clean build -x test

# 5. 환경 재시작
./run-load-test.sh

echo "✅ 복구 완료!"
```

### 백업 실행 방법
```bash
# Docker 없이 직접 실행 (비상용)
java -jar build/libs/stock-portfolio-backtest-api-*.jar --spring.profiles.active=dev
```

---

## 📞 추가 지원

### 로그 파일 위치
- nGrinder Controller: `/opt/ngrinder-controller/logs/`
- nGrinder Agent: `/opt/ngrinder-agent/logs/`
- 애플리케이션: Docker 컨테이너 내부 (`docker logs` 사용)

### 유용한 디버깅 도구
```bash
# 네트워크 연결 테스트
telnet localhost 8080
nc -zv localhost 8081

# 프로세스 모니터링
top -p $(pgrep java)

# 디스크 사용량
du -sh /var/lib/docker/
```

---

**🎯 대부분의 문제는 위 가이드로 해결됩니다. 그래도 안 되면 전체 리셋을 시도해보세요!**