#!/bin/bash

# nGrinder 부하테스트 실행 스크립트
# 포트폴리오 백테스팅 API 전용

echo "🚀 포트폴리오 백테스팅 API 부하테스트 시작"
echo "===================================================="

# 1. Docker 환경 확인
if ! command -v docker-compose &> /dev/null; then
    echo "❌ docker-compose가 설치되어 있지 않습니다."
    exit 1
fi

# 2. 애플리케이션 빌드
echo "📦 애플리케이션 빌드 중..."
./gradlew build -x test

if [ $? -ne 0 ]; then
    echo "❌ 애플리케이션 빌드 실패"
    exit 1
fi

# 3. nGrinder 환경 시작
echo "🔧 nGrinder 환경 시작..."
docker-compose -f docker-compose.ngrinder.yml up -d

# 4. 서비스 준비 대기
echo "⏳ 서비스 준비 대기 중..."
sleep 30

# 5. API 서버 Health Check
echo "🩺 API 서버 상태 확인..."
max_attempts=10
attempt=1

while [ $attempt -le $max_attempts ]; do
    if curl -f http://localhost:8081/api/v1/portfolios/backtest -X POST \
           -H "Content-Type: application/json" \
           -d '{"name":"test","amount":1000000,"startDate":"2023-01-01","endDate":"2023-12-31","portfolioBacktestRequestItemDTOList":[{"stockId":1,"weight":100.0}]}' &> /dev/null; then
        echo "✅ API 서버 준비 완료"
        break
    fi
    
    echo "⏳ API 서버 준비 중... (시도: $attempt/$max_attempts)"
    sleep 10
    ((attempt++))
done

if [ $attempt -gt $max_attempts ]; then
    echo "❌ API 서버 준비 시간 초과"
    docker-compose -f docker-compose.ngrinder.yml logs portfolio-api
    exit 1
fi

# 6. 테스트 시나리오 안내
echo ""
echo "🎯 테스트 시나리오"
echo "===================="
echo "1. 📊 종합 부하테스트 (PortfolioBacktestLoadTest.groovy)"
echo "   - 사용자 인증 + 백테스팅 + 저장 + 조회"
echo "   - 실제 사용자 플로우 시뮬레이션"
echo "   - 동시 사용자: 50명, 지속시간: 5분"
echo ""
echo "2. 🔥 스트레스 테스트 (BacktestStressTest.groovy)"  
echo "   - CPU 집약적 백테스팅 전용"
echo "   - 가벼운(60%) + 무거운(30%) + 극한(10%) 시나리오"
echo "   - 시스템 한계 테스트"
echo ""

# 7. nGrinder Web UI 안내
echo "🌐 nGrinder Web UI 접속 정보"
echo "==============================="
echo "URL: http://localhost:8080"
echo "기본 계정: admin / admin"
echo ""
echo "📋 테스트 실행 방법:"
echo "1. Web UI 접속"
echo "2. 'Script' 메뉴에서 스크립트 업로드"
echo "3. 'Performance Test' 메뉴에서 테스트 생성"
echo "4. 'Agent Management'에서 Agent 확인"
echo ""

# 8. 테스트 스크립트 업로드 안내
echo "📄 업로드할 테스트 스크립트:"
echo "- ngrinder-scripts/PortfolioBacktestLoadTest.groovy"
echo "- ngrinder-scripts/BacktestStressTest.groovy"
echo "- ngrinder-scripts/test-config.properties"
echo ""

# 9. 성능 모니터링 안내
echo "📈 성능 모니터링 도구"
echo "====================="
echo "• Application Metrics: http://localhost:8081/actuator/metrics"
echo "• H2 Database Console: http://localhost:8081/h2-console"
echo "• JVM 메모리 사용량: docker stats portfolio-api-test"
echo ""

# 10. 테스트 결과 분석 포인트
echo "🔍 주요 분석 포인트"
echo "==================="
echo "1. TPS (Transactions Per Second)"
echo "2. 평균/최대 응답시간"  
echo "3. 95th Percentile 응답시간"
echo "4. 에러율"
echo "5. CPU/메모리 사용률"
echo "6. 데이터베이스 커넥션 풀 상태"
echo ""

# 11. 권장 테스트 시나리오
echo "💡 권장 테스트 시나리오"
echo "======================="
echo "Phase 1: 기본 성능 테스트"
echo "  - 동시 사용자: 10명"
echo "  - 지속 시간: 2분"
echo "  - 목표: 기본 기능 동작 확인"
echo ""
echo "Phase 2: 부하 테스트"  
echo "  - 동시 사용자: 50명"
echo "  - 지속 시간: 5분"
echo "  - 목표: 정상 운영 부하 테스트"
echo ""
echo "Phase 3: 스트레스 테스트"
echo "  - 동시 사용자: 100명"
echo "  - 지속 시간: 10분"
echo "  - 목표: 시스템 한계점 확인"
echo ""

echo "✨ nGrinder 환경이 준비되었습니다!"
echo "웹 브라우저에서 http://localhost:8080 으로 접속하여 테스트를 시작하세요."

# 12. 종료 스크립트 안내
echo ""
echo "🛑 테스트 종료 방법:"
echo "docker-compose -f docker-compose.ngrinder.yml down"