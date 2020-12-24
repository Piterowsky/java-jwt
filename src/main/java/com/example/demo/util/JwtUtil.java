package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Log4j2
public class JwtUtil {

    private static final String SECRET_KEY = "secret";

    private JwtUtil() {
    }

    public static String generateToken(UserDetails userDetails) {
        var claims = new HashMap<String, Object>();
        return createToken(claims, userDetails.getUsername());
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        var claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private static String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private static Date getExpirationDate() {
        var hourInMillis = 3_600_000;
        return new Date(System.currentTimeMillis() + hourInMillis);
    }

    public static boolean validateToken(String token, UserDetails userDetails) {
        var usernameFromDatabase = userDetails.getUsername();
        var usernameFromToken = extractUsername(token);
        return usernameFromDatabase.equals(usernameFromToken) && !isTokenExpired(token);
    }

    private static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
