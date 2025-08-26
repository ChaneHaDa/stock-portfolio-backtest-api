# ⚡ 빠른 시작 가이드 - nGrinder 부하테스트

## 🎯 3분 만에 부하테스트 실행하기

### 1️⃣ 사전 체크 (30초)
```bash
# 필수 도구 확인
docker --version && docker-compose --version && java -version
```

### 2️⃣ 원클릭 실행 (2분)
```bash
# 프로젝트 디렉토리로 이동
cd /home/ind/code/st-pro/stock-portfolio-backtest-api

# 모든 환경 자동 설정 및 시작
./run-load-test.sh
```

### 3️⃣ 테스트 시작 (30초)
1. 브라우저에서 **http://localhost:8080** 접속
2. `admin` / `admin` 으로 로그인
3. **Performance Test** → **Create Test** 클릭
4. 스크립트 선택 후 **Start** 버튼!

---

## 🚨 문제 발생 시 체크리스트

### ❌ 포트 충돌 에러
```bash
sudo pkill -f "8080\|8081\|8082"
./run-load-test.sh
```

### ❌ Docker 에러
```bash
docker-compose -f docker-compose.ngrinder.yml down
docker system prune -f
./run-load-test.sh
```

### ❌ 메모리 부족
```bash
# docker-compose.ngrinder.yml 수정
environment:
  - JAVA_OPTS=-Xmx2g
```

---

## 📊 결과 확인

### 실시간 성능 체크
```bash
./analyze-performance.sh
```

### 핵심 지표 목표값
- **TPS**: 100+ (초당 처리량)
- **응답시간**: 평균 2초 이하
- **에러율**: 1% 미만
- **CPU**: 80% 이하

---

## 🎁 보너스 팁

### 빠른 테스트 설정
```
Virtual Users: 10 → 50 → 100 (단계적 증가)
Duration: 2분 → 5분 → 10분
Target: portfolio-api:8080
```

### 실패 시 로그 확인
```bash
docker logs portfolio-api-test
docker logs ngrinder-controller
```

---

**💡 더 자세한 설정은 [LOAD_TEST_GUIDE.md](./LOAD_TEST_GUIDE.md) 참고!**