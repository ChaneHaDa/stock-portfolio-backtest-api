#!/bin/bash

# 부하테스트 결과 분석 스크립트
# nGrinder 테스트 완료 후 성능 분석

echo "📊 포트폴리오 백테스팅 API 성능 분석"
echo "===================================="

API_URL="http://localhost:8081"
ACTUATOR_URL="http://localhost:8082/actuator"

# 1. API 서버 상태 확인
echo "🩺 1. API 서버 상태 확인"
echo "------------------------"
curl -s $ACTUATOR_URL/health | jq '.'
echo ""

# 2. JVM 메트릭스 확인
echo "💾 2. JVM 메모리 사용량"
echo "------------------------"
echo "힙 메모리 사용량:"
curl -s $ACTUATOR_URL/metrics/jvm.memory.used?tag=area:heap | jq '.measurements[0].value / 1024 / 1024 | floor' | xargs echo "MB"

echo "힙 메모리 최대값:"
curl -s $ACTUATOR_URL/metrics/jvm.memory.max?tag=area:heap | jq '.measurements[0].value / 1024 / 1024 | floor' | xargs echo "MB"

echo "GC 횟수:"
curl -s $ACTUATOR_URL/metrics/jvm.gc.pause | jq '.measurements[0].value' | xargs echo "times"
echo ""

# 3. HTTP 요청 메트릭스
echo "🌐 3. HTTP 요청 통계"
echo "-------------------"
echo "총 HTTP 요청 수:"
curl -s $ACTUATOR_URL/metrics/http.server.requests | jq '.measurements[] | select(.statistic == "COUNT") | .value' 2>/dev/null | head -1 || echo "N/A"

echo "평균 응답시간:"
curl -s $ACTUATOR_URL/metrics/http.server.requests | jq '.measurements[] | select(.statistic == "TOTAL_TIME") | .value' 2>/dev/null | head -1 | awk '{print $1/1000 "ms"}' || echo "N/A"
echo ""

# 4. 데이터베이스 커넥션 풀 상태
echo "🗃️ 4. 데이터베이스 커넥션 풀"
echo "----------------------------"
echo "활성 커넥션:"
curl -s $ACTUATOR_URL/metrics/hikaricp.connections.active | jq '.measurements[0].value' 2>/dev/null || echo "N/A"

echo "유휴 커넥션:"
curl -s $ACTUATOR_URL/metrics/hikaricp.connections.idle | jq '.measurements[0].value' 2>/dev/null || echo "N/A"

echo "최대 커넥션:"
curl -s $ACTUATOR_URL/metrics/hikaricp.connections.max | jq '.measurements[0].value' 2>/dev/null || echo "N/A"
echo ""

# 5. 시스템 리소스 사용량
echo "🖥️ 5. 시스템 리소스"
echo "-------------------"
echo "CPU 사용률:"
curl -s $ACTUATOR_URL/metrics/system.cpu.usage | jq '.measurements[0].value * 100 | floor' 2>/dev/null | xargs echo "%" || echo "N/A"

echo "시스템 로드:"
curl -s $ACTUATOR_URL/metrics/system.load.average.1m | jq '.measurements[0].value' 2>/dev/null || echo "N/A"
echo ""

# 6. Docker 컨테이너 리소스 사용량
echo "🐳 6. Docker 컨테이너 리소스"
echo "----------------------------"
if command -v docker &> /dev/null; then
    echo "포트폴리오 API 컨테이너:"
    docker stats portfolio-api-test --no-stream --format "CPU: {{.CPUPerc}}, 메모리: {{.MemUsage}}" 2>/dev/null || echo "컨테이너를 찾을 수 없습니다"
    echo ""
fi

# 7. 성능 개선 권장사항
echo "💡 7. 성능 개선 권장사항"
echo "------------------------"

# 메모리 사용량 체크
HEAP_USED=$(curl -s $ACTUATOR_URL/metrics/jvm.memory.used?tag=area:heap | jq '.measurements[0].value' 2>/dev/null || echo 0)
HEAP_MAX=$(curl -s $ACTUATOR_URL/metrics/jvm.memory.max?tag=area:heap | jq '.measurements[0].value' 2>/dev/null || echo 1)
HEAP_USAGE_PERCENT=$(echo "scale=2; $HEAP_USED / $HEAP_MAX * 100" | bc -l 2>/dev/null || echo 0)

if (( $(echo "$HEAP_USAGE_PERCENT > 80" | bc -l) )); then
    echo "⚠️  힙 메모리 사용률이 높습니다 (${HEAP_USAGE_PERCENT}%)"
    echo "   - JVM 힙 크기 증가 권장: -Xmx2g"
    echo "   - 메모리 프로파일링 필요"
fi

# CPU 사용률 체크  
CPU_USAGE=$(curl -s $ACTUATOR_URL/metrics/system.cpu.usage | jq '.measurements[0].value * 100' 2>/dev/null || echo 0)
if (( $(echo "$CPU_USAGE > 80" | bc -l 2>/dev/null) )); then
    echo "⚠️  CPU 사용률이 높습니다 (${CPU_USAGE}%)"
    echo "   - 백테스팅 알고리즘 최적화 필요"
    echo "   - 비동기 처리 적용 권장"
fi

# 8. 성능 최적화 체크리스트
echo ""
echo "✅ 8. 성능 최적화 체크리스트"
echo "----------------------------"
echo "□ Redis 캐싱 구현 (백테스팅 결과 캐싱)"
echo "□ 데이터베이스 인덱스 최적화"
echo "□ 커넥션 풀 크기 조정"
echo "□ JVM 튜닝 (GC 설정)"
echo "□ 비동기 처리 도입"
echo "□ API 응답 압축 (gzip)"
echo "□ CDN 도입 (정적 리소스)"
echo "□ 로드 밸런서 설정"
echo ""

# 9. 추가 모니터링 도구 안내
echo "📈 9. 추가 모니터링 도구"
echo "------------------------"
echo "• Prometheus + Grafana 대시보드 구성"
echo "• APM 도구 (New Relic, DataDog, Pinpoint)"
echo "• 로그 분석 (ELK Stack)"
echo "• 데이터베이스 모니터링 (PgAdmin, H2 Console)"
echo ""

echo "🎯 분석 완료!"
echo "nGrinder 웹 UI에서 TPS, 응답시간, 에러율을 함께 확인하세요."
echo "Web UI: http://localhost:8080"