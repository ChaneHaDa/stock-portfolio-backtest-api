import net.grinder.script.GTest
import net.grinder.script.Grinder
import net.grinder.scriptengine.groovy.junit.GrinderRunner
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread
import org.junit.Test
import org.junit.runner.RunWith

import HTTPClient.HTTPRequest
import HTTPClient.NVPair
import HTTPClient.HTTPResponse
import net.grinder.plugin.http.HTTPPluginControl

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

/**
 * 백테스팅 API 스트레스 테스트
 * CPU 집약적 작업에 대한 고부하 테스트
 */
@RunWith(GrinderRunner)
class BacktestStressTest {

    public static GTest heavyBacktestTest
    public static GTest lightBacktestTest
    public static GTest extremeBacktestTest
    
    public static HTTPRequest heavyBacktestRequest
    public static HTTPRequest lightBacktestRequest  
    public static HTTPRequest extremeBacktestRequest
    
    public static String baseUrl = "http://portfolio-api:8080"
    public static List<Map> testScenarios = []
    
    @BeforeProcess
    public static void beforeProcess() {
        HTTPPluginControl.getConnectionDefaults().timeout = 60000  // 1분 타임아웃
        
        // 다양한 백테스팅 시나리오 준비
        testScenarios = [
            createLightPortfolio(),      // 가벼운 계산 (3개 종목, 1년)
            createHeavyPortfolio(),      // 무거운 계산 (10개 종목, 5년)  
            createExtremePortfolio()     // 극한 계산 (20개 종목, 10년)
        ]
        
        heavyBacktestTest = new GTest(1, "고부하 백테스팅")
        lightBacktestTest = new GTest(2, "일반 백테스팅")
        extremeBacktestTest = new GTest(3, "극한 백테스팅")
        
        heavyBacktestRequest = heavyBacktestTest.wrap(new HTTPRequest())
        lightBacktestRequest = lightBacktestTest.wrap(new HTTPRequest())
        extremeBacktestRequest = extremeBacktestTest.wrap(new HTTPRequest())
        
        Grinder.logger.info("백테스팅 스트레스 테스트 초기화 완료")
    }

    @BeforeThread
    public void beforeThread() {
        def headers = [
            new NVPair("Content-Type", "application/json"),
            new NVPair("Accept", "application/json")
        ] as NVPair[]
        
        heavyBacktestRequest.setHeaders(headers)
        lightBacktestRequest.setHeaders(headers)
        extremeBacktestRequest.setHeaders(headers)
    }

    /**
     * 혼합 부하 테스트
     * 실제 운영 환경의 다양한 요청 패턴 시뮬레이션
     */
    @Test
    public void testMixedWorkload() {
        int threadNum = Grinder.grinder.threadNumber
        int scenario = threadNum % 10
        
        if (scenario < 6) {
            // 60% - 가벼운 백테스팅
            performLightBacktest()
        } else if (scenario < 9) {
            // 30% - 무거운 백테스팅  
            performHeavyBacktest()
        } else {
            // 10% - 극한 백테스팅
            performExtremeBacktest()
        }
    }
    
    /**
     * 순수 고부하 테스트
     * CPU 한계 테스트용
     */
    @Test
    public void testCpuIntensive() {
        // 모든 요청이 복잡한 백테스팅
        performHeavyBacktest()
    }
    
    /**
     * 메모리 압박 테스트
     * 대용량 포트폴리오 동시 처리
     */
    @Test  
    public void testMemoryPressure() {
        performExtremeBacktest()
    }

    // === Private Methods ===
    
    private void performLightBacktest() {
        def portfolio = testScenarios[0]  // 가벼운 포트폴리오
        executeBacktest(lightBacktestRequest, portfolio, "LIGHT")
    }
    
    private void performHeavyBacktest() {
        def portfolio = testScenarios[1]  // 무거운 포트폴리오
        executeBacktest(heavyBacktestRequest, portfolio, "HEAVY")
    }
    
    private void performExtremeBacktest() {
        def portfolio = testScenarios[2]  // 극한 포트폴리오
        executeBacktest(extremeBacktestRequest, portfolio, "EXTREME")
    }
    
    private void executeBacktest(HTTPRequest request, Map portfolio, String type) {
        def jsonData = new JsonBuilder(portfolio).toString()
        
        long startTime = System.currentTimeMillis()
        
        try {
            HTTPResponse response = request.POST("${baseUrl}/api/v1/portfolios/backtest", jsonData)
            
            long responseTime = System.currentTimeMillis() - startTime
            
            if (response.statusCode == 200) {
                def result = new JsonSlurper().parseText(response.text)
                logSuccess(type, responseTime, result.data)
            } else {
                Grinder.logger.error("{} 백테스팅 실패 - 상태코드: {}, 응답시간: {}ms", 
                    type, response.statusCode, responseTime)
            }
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime
            Grinder.logger.error("{} 백테스팅 예외 - 응답시간: {}ms, 에러: {}", 
                type, responseTime, e.message)
        }
    }
    
    private void logSuccess(String type, long responseTime, Map result) {
        Grinder.logger.info("{} 백테스팅 성공 - 응답시간: {}ms, 수익률: {}%, 변동성: {}%",
            type, responseTime, result.totalRor, result.volatility)
            
        // 성능 임계값 체크
        if (responseTime > getPerformanceThreshold(type)) {
            Grinder.logger.warn("{} 백테스팅 성능 경고 - 임계값 초과: {}ms", type, responseTime)
        }
    }
    
    private long getPerformanceThreshold(String type) {
        switch(type) {
            case "LIGHT": return 1000    // 1초
            case "HEAVY": return 5000    // 5초  
            case "EXTREME": return 10000 // 10초
            default: return 5000
        }
    }
    
    // === 테스트 데이터 생성 ===
    
    private static Map createLightPortfolio() {
        return [
            name: "가벼운 포트폴리오",
            description: "3개 종목, 1년 백테스팅",
            amount: 1000000L,
            startDate: "2023-01-01",
            endDate: "2023-12-31",
            portfolioBacktestRequestItemDTOList: [
                [stockId: 1, weight: 50.0],
                [stockId: 2, weight: 30.0],
                [stockId: 3, weight: 20.0]
            ]
        ]
    }
    
    private static Map createHeavyPortfolio() {
        def items = []
        double weightPerStock = 100.0 / 10  // 10개 종목 균등분할
        
        (1..10).each { stockId ->
            items << [stockId: stockId, weight: weightPerStock]
        }
        
        return [
            name: "무거운 포트폴리오",
            description: "10개 종목, 5년 백테스팅", 
            amount: 10000000L,
            startDate: "2019-01-01",
            endDate: "2023-12-31",
            portfolioBacktestRequestItemDTOList: items
        ]
    }
    
    private static Map createExtremePortfolio() {
        def items = []
        double weightPerStock = 100.0 / 20  // 20개 종목 균등분할
        
        (1..20).each { stockId ->
            items << [stockId: stockId, weight: weightPerStock]
        }
        
        return [
            name: "극한 포트폴리오",
            description: "20개 종목, 10년 백테스팅",
            amount: 50000000L,
            startDate: "2014-01-01", 
            endDate: "2023-12-31",
            portfolioBacktestRequestItemDTOList: items
        ]
    }
}