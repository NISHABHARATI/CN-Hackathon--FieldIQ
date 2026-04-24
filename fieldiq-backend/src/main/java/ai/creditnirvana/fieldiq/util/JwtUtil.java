package ai.creditnirvana.fieldiq.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long EXPIRY_MS = 12 * 60 * 60 * 1000L; // 12 hours
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generate(Long agentId, String name, String role, String zone, String phone) {
        return Jwts.builder()
                .setSubject(String.valueOf(agentId))
                .claim("name", name)
                .claim("role", role)
                .claim("zone", zone)
                .claim("phone", phone)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}