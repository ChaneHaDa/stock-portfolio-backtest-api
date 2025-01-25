package com.chan.stock_portfolio_backtest_api.util;

import com.chan.stock_portfolio_backtest_api.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JWTUtil {
    private static final long EXPIRATION_MILLIS = 30 * 60 * 1000;

    public static String createToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MILLIS);

        SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject("Portfolio")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("username", username)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private static Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
