package com.chan.stock_portfolio_backtest_api.data;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class EmailVerificationQueue {
    // 이메일을 키로, 토큰 정보를 저장
    private final Map<String, EmailVerificationToken> tokenMap = new ConcurrentHashMap<>();

    // 만료 시간 기준으로 정렬된 큐
    private final PriorityQueue<EmailVerificationToken> expiryQueue = new PriorityQueue<>(
            Comparator.comparing(EmailVerificationToken::getExpiresAt)
    );

    public synchronized void add(String email, String token, int ttlMinutes) {
        if (tokenMap.containsKey(email)) {
            EmailVerificationToken oldEntry = tokenMap.get(email);
            expiryQueue.remove(oldEntry);
        }
        EmailVerificationToken entry = EmailVerificationToken.builder()
                        .email(email)
                        .token(token)
                        .isVerified(false)
                        .expiresAt(LocalDateTime.now().plusMinutes(ttlMinutes))
                        .build();

        tokenMap.put(email, entry);
        expiryQueue.offer(entry);
    }

    public synchronized boolean verify(String email, String token) {
        cleanupExpired();
        EmailVerificationToken entry = tokenMap.get(email);
        if (entry != null && entry.getToken().equals(token)) {
            entry.setIsVerified(true);
            return true;
        }
        return false;
    }

    public synchronized boolean isVerify(String email) {
        cleanupExpired();
        EmailVerificationToken entry = tokenMap.get(email);
        return entry != null && entry.getIsVerified();
    }

    private synchronized void cleanupExpired() {
        LocalDateTime now = LocalDateTime.now();
        while (!expiryQueue.isEmpty() && expiryQueue.peek().getExpiresAt().isBefore(now)) {
            EmailVerificationToken expired = expiryQueue.poll();
            // Map에서도 해당 이메일 토큰이 만료된 토큰과 일치하면 제거
            tokenMap.remove(expired.getEmail(), expired);
        }
    }

    public synchronized boolean containsEmail(String email) {
        cleanupExpired();
        return tokenMap.containsKey(email);
    }
}