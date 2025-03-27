package com.chan.stock_portfolio_backtest_api.data;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;

public class EmailVerificationQueue {
    private final PriorityQueue<EmailVerificationToken> queue = new PriorityQueue<>(
            Comparator.comparing(EmailVerificationToken::getExpiresAt)
    );

    public void add(String email, String token, int ttlMinutes) {
        EmailVerificationToken entry = new EmailVerificationToken(
                email,
                token,
                LocalDateTime.now().plusMinutes(ttlMinutes)
        );
        queue.offer(entry);
    }

    public boolean verify(String email, String token) {
        cleanupExpired();
        return queue.stream()
                .anyMatch(e -> e.getEmail().equals(email) && e.getToken().equals(token));
    }

    private void cleanupExpired() {
        while (!queue.isEmpty() && queue.peek().isExpired()) {
            queue.poll();
        }
    }

    private boolean isInEmail(String email) {
        cleanupExpired();
        return queue.stream().anyMatch(e -> e.getEmail().equals(email));
    }
}