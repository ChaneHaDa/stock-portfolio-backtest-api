import net.grinder.script.GTest
import net.grinder.script.Grinder
import net.grinder.scriptengine.groovy.junit.GrinderRunner
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import java.util.Date
import java.util.List
import java.util.ArrayList

import HTTPClient.HTTPClient
import HTTPClient.NVPair
import HTTPClient.CookieModule
import HTTPClient.HTTPResponse
import net.grinder.plugin.http.HTTPRequest
import net.grinder.plugin.http.HTTPPluginControl
import net.grinder.common.GrinderProperties
import net.grinder.util.GrinderUtils

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

/**
 * 포트폴리오 백테스팅 API 부하테스트 스크립트
 * 
 * 테스트 시나리오:
 * 1. 사용자 로그인
 * 2. 포트폴리오 백테스팅 요청
 * 3. 포트폴리오 저장
 * 4. 포트폴리오 조회
 */
@RunWith(GrinderRunner)
class PortfolioBacktestLoadTest {

    public static GTest loginTest
    public static GTest backtestTest  
    public static GTest savePortfolioTest
    public static GTest getPortfolioTest
    
    public static HTTPRequest loginRequest
    public static HTTPRequest backtestRequest
    public static HTTPRequest savePortfolioRequest
    public static HTTPRequest getPortfolioRequest
    
    // 테스트 설정
    public static String baseUrl = "http://portfolio-api:8080"  // Docker 네트워크 내부 URL
    public static List<Map> testUsers = []
    public static List<Map> testPortfolios = []
    
    @BeforeProcess
    public static void beforeProcess() {
        HTTPPluginControl.getConnectionDefaults().timeout = 30000
        
        // 테스트용 사용자 데이터 준비
        testUsers = [
            [email: "test1@example.com", password: "password123"],
            [email: "test2@example.com", password: "password123"],
            [email: "test3@example.com", password: "password123"]
        ]
        
        // 테스트용 포트폴리오 데이터 준비  
        testPortfolios = [
            createSamplePortfolio("테크 중심 포트폴리오", 1000000L),
            createSamplePortfolio("균형 포트폴리오", 2000000L),
            createSamplePortfolio("성장 포트폴리오", 5000000L)
        ]
        
        // GTest 객체 생성 (성능 측정용)
        loginTest = new GTest(1, "사용자 로그인")
        backtestTest = new GTest(2, "포트폴리오 백테스팅")
        savePortfolioTest = new GTest(3, "포트폴리오 저장")
        getPortfolioTest = new GTest(4, "포트폴리오 조회")
        
        // HTTP 요청 객체 생성
        loginRequest = loginTest.wrap(new HTTPRequest())
        backtestRequest = backtestTest.wrap(new HTTPRequest())  
        savePortfolioRequest = savePortfolioTest.wrap(new HTTPRequest())
        getPortfolioRequest = getPortfolioTest.wrap(new HTTPRequest())
        
        Grinder.logger.info("포트폴리오 백테스팅 부하테스트 초기화 완료")
    }

    @BeforeThread 
    public void beforeThread() {
        loginRequest.setHeaders(getJsonHeaders())
        backtestRequest.setHeaders(getJsonHeaders())
        savePortfolioRequest.setHeaders(getJsonHeaders()) 
        getPortfolioRequest.setHeaders(getJsonHeaders())
        
        Grinder.logger.info("스레드 #{} 초기화 완료", 
            Grinder.grinder.threadNumber)
    }

    @Before
    public void setUp() {
        // 각 테스트 실행 전 준비 작업
    }

    /**
     * 메인 테스트 시나리오
     * 실제 사용자 플로우를 시뮬레이션
     */
    @Test
    public void testPortfolioBacktestFlow() {
        // 1. 사용자 선택 (라운드 로빈)
        def user = testUsers[Grinder.grinder.threadNumber % testUsers.size()]
        def portfolio = testPortfolios[Grinder.grinder.threadNumber % testPortfolios.size()]
        
        // 2. 로그인
        String jwtToken = performLogin(user)
        if (!jwtToken) {
            Grinder.logger.error("로그인 실패: {}", user.email)
            return
        }
        
        // JWT 토큰을 헤더에 추가
        updateHeadersWithAuth(jwtToken)
        
        // 3. 백테스팅 수행 (가장 중요한 API)
        def backtestResult = performBacktest(portfolio)
        if (!backtestResult) {
            Grinder.logger.error("백테스팅 실패")
            return  
        }
        
        // 4. 백테스팅 결과로 포트폴리오 저장
        def savedPortfolio = savePortfolio(backtestResult)
        if (!savedPortfolio) {
            Grinder.logger.error("포트폴리오 저장 실패")
            return
        }
        
        // 5. 저장된 포트폴리오 조회
        getPortfolio()
        
        Grinder.logger.info("테스트 플로우 완료 - Thread: {}", 
            Grinder.grinder.threadNumber)
    }
    
    /**
     * 백테스팅 전용 고강도 테스트
     * CPU 집약적 작업 부하 테스트
     */
    @Test  
    public void testBacktestOnly() {
        def portfolio = testPortfolios[Grinder.grinder.runNumber % testPortfolios.size()]
        
        // 인증 없는 백테스팅 API 호출 (public endpoint)
        def result = performBacktest(portfolio)
        
        if (result) {
            Grinder.logger.info("백테스팅 완료 - 수익률: {}%, 변동성: {}%", 
                result.totalRor, result.volatility)
        }
    }

    // === Private Methods ===
    
    private String performLogin(Map user) {
        def loginData = new JsonBuilder([
            email: user.email,
            password: user.password  
        ]).toString()
        
        try {
            HTTPResponse response = loginRequest.POST("${baseUrl}/api/v1/auth/login", loginData)
            
            if (response.statusCode == 200) {
                def jsonResponse = new JsonSlurper().parseText(response.text)
                return jsonResponse.data.token
            } else {
                Grinder.logger.error("로그인 실패 - 상태코드: {}, 응답: {}", 
                    response.statusCode, response.text)
            }
        } catch (Exception e) {
            Grinder.logger.error("로그인 요청 예외: {}", e.message)
        }
        return null
    }
    
    private Map performBacktest(Map portfolio) {
        def backtestData = new JsonBuilder(portfolio).toString()
        
        try {
            HTTPResponse response = backtestRequest.POST("${baseUrl}/api/v1/portfolios/backtest", backtestData)
            
            if (response.statusCode == 200) {
                def jsonResponse = new JsonSlurper().parseText(response.text)
                return jsonResponse.data
            } else {
                Grinder.logger.error("백테스팅 실패 - 상태코드: {}, 응답: {}", 
                    response.statusCode, response.text)
            }
        } catch (Exception e) {
            Grinder.logger.error("백테스팅 요청 예외: {}", e.message)
        }
        return null
    }
    
    private Map savePortfolio(Map backtestResult) {
        // 백테스팅 결과를 포트폴리오 저장 형태로 변환
        def portfolioData = convertBacktestToPortfolio(backtestResult)
        def jsonData = new JsonBuilder(portfolioData).toString()
        
        try {
            HTTPResponse response = savePortfolioRequest.POST("${baseUrl}/api/v1/portfolios", jsonData)
            
            if (response.statusCode == 201) {
                def jsonResponse = new JsonSlurper().parseText(response.text)
                return jsonResponse.data
            } else {
                Grinder.logger.error("포트폴리오 저장 실패 - 상태코드: {}", response.statusCode)
            }
        } catch (Exception e) {
            Grinder.logger.error("포트폴리오 저장 예외: {}", e.message)
        }
        return null
    }
    
    private void getPortfolio() {
        try {
            HTTPResponse response = getPortfolioRequest.GET("${baseUrl}/api/v1/portfolios")
            
            if (response.statusCode != 200) {
                Grinder.logger.error("포트폴리오 조회 실패 - 상태코드: {}", response.statusCode)
            }
        } catch (Exception e) {
            Grinder.logger.error("포트폴리오 조회 예외: {}", e.message)
        }
    }
    
    private void updateHeadersWithAuth(String token) {
        def authHeaders = getJsonHeaders()
        authHeaders << new NVPair("Authorization", "Bearer ${token}")
        
        backtestRequest.setHeaders(authHeaders)
        savePortfolioRequest.setHeaders(authHeaders)
        getPortfolioRequest.setHeaders(authHeaders)
    }
    
    private static NVPair[] getJsonHeaders() {
        return [
            new NVPair("Content-Type", "application/json"),
            new NVPair("Accept", "application/json"),
            new NVPair("User-Agent", "nGrinder-LoadTest")
        ] as NVPair[]
    }
    
    private static Map createSamplePortfolio(String name, Long amount) {
        return [
            name: name,
            description: "부하테스트용 ${name}",
            amount: amount,
            startDate: "2023-01-01",
            endDate: "2024-01-01",
            portfolioBacktestRequestItemDTOList: [
                [
                    stockId: 1,
                    weight: 40.0
                ],
                [
                    stockId: 2, 
                    weight: 30.0
                ],
                [
                    stockId: 3,
                    weight: 30.0
                ]
            ]
        ]
    }
    
    private Map convertBacktestToPortfolio(Map backtestResult) {
        def input = backtestResult.portfolioInput
        return [
            name: input.name,
            description: input.description,
            amount: input.amount,
            startDate: input.startDate,
            endDate: input.endDate,
            ror: backtestResult.totalRor,
            volatility: backtestResult.volatility,
            price: (backtestResult.totalAmount / input.amount).floatValue(),
            portfolioItemRequestDTOList: input.portfolioBacktestRequestItemDTOList.collect { item ->
                [
                    stockId: item.stockId,
                    weight: item.weight
                ]
            }
        ]
    }
}