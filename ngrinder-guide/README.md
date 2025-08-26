# 📚 nGrinder 부하테스트 완벽 가이드

## 📁 파일 구조
```
stock-portfolio-backtest-api/
├── 📋 QUICK_START.md              # 3분 빠른 시작 가이드
├── 📖 LOAD_TEST_GUIDE.md          # 상세 부하테스트 가이드
├── 🐳 docker-compose.ngrinder.yml # Docker 환경 설정
├── 🚀 run-load-test.sh            # 자동 실행 스크립트
├── 📊 analyze-performance.sh      # 성능 분석 스크립트
├── ngrinder-scripts/              # 테스트 스크립트 모음
│   ├── PortfolioBacktestLoadTest.groovy  # 종합 부하테스트
│   ├── BacktestStressTest.groovy         # 스트레스 테스트
│   └── test-config.properties           # 테스트 설정
└── ngrinder-guide/                # 가이드 문서 모음
    └── README.md                  # 이 파일
```

## 🎯 실행 순서

### 🚀 초보자 (추천)
1. **[QUICK_START.md](../QUICK_START.md)** 읽기
2. `./run-load-test.sh` 실행
3. http://localhost:8080 접속하여 테스트

### 🔧 고급 사용자
1. **[LOAD_TEST_GUIDE.md](../LOAD_TEST_GUIDE.md)** 전체 읽기
2. 수동으로 환경 구성 및 커스터마이징
3. 상세 성능 분석 및 최적화

## 🛠️ 주요 명령어 모음

```bash
# 환경 시작
./run-load-test.sh

# 성능 분석
./analyze-performance.sh

# 환경 정리
docker-compose -f docker-compose.ngrinder.yml down

# 로그 확인
docker logs portfolio-api-test
```

## 📞 지원

- 🐛 **문제 발생**: [LOAD_TEST_GUIDE.md - 문제 해결](../LOAD_TEST_GUIDE.md#문제-해결) 섹션 참고
- ⚡ **빠른 시작**: [QUICK_START.md](../QUICK_START.md) 참고
- 🚀 **성능 최적화**: [LOAD_TEST_GUIDE.md - 성능 최적화](../LOAD_TEST_GUIDE.md#성능-최적화-가이드) 참고

---
**✨ Happy Load Testing! 🎉**